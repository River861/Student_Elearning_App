from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options
from flask import Flask, request
from flask_cors import CORS
import requests
import json
import datetime
import traceback
import logging


app = Flask(__name__)
logging.basicConfig(filename='./log.txt', level=logging.ERROR,
                    format='%(asctime)s - %(name)s - %(filename)s[line:%(lineno)d] - %(levelname)s - %(message)s')


ELEARNING = "https://elearning.fudan.edu.cn"
RESULT_OK = 0
RESULT_ERROR = 1
MSG_OK = 'ok'


@app.route('/hello')
def hello():
    return "hello~~~~ ^_^"


def getOptions():
    chrome_options = Options()
    chrome_options.add_argument('--no-sandbox')
    chrome_options.add_argument('--disable-gpu')
    chrome_options.add_argument('--hide-scrollbars')
    chrome_options.add_argument('blink-settings=imagesEnabled=false')
    chrome_options.add_argument('--headless')
    return chrome_options


def getEle(driver, byWhat, val):
    ele = WebDriverWait(driver, 5, 0.5).until(EC.visibility_of_element_located((byWhat, val)))
    return ele


@app.route('/getElearningToken', methods=['POST'])
def getElearningToken():
    if request.method == 'POST':
        dic = json.loads(request.data)
        username = dic['username']
        password = dic['password']

        driver = webdriver.Chrome(options=getOptions())
        driver.get('https://uis.fudan.edu.cn/authserver/login?service=https%3A%2F%2Felearning.fudan.edu.cn%2Flogin%2Fcas%2F3')
        try:
            driver.maximize_window()
            getEle(driver, By.CLASS_NAME, 'IDCheckLoginName').send_keys(username)
            getEle(driver, By.CLASS_NAME, 'IDCheckLoginPassWord').send_keys(password)
            getEle(driver, By.CLASS_NAME, 'IDCheckLoginBtn').click()
            getEle(driver, By.CLASS_NAME, 'ic-app-header__menu-list-item').click()
            getEle(driver, By.LINK_TEXT, '设置').click()
            token = None
            while token is None:
                driver.refresh()
                getEle(driver, By.CLASS_NAME, 'add_access_token_link').click()
                getEle(driver, By.NAME, 'access_token[purpose]').send_keys('Elearning-App')
                getEle(driver, By.CLASS_NAME, 'button_type_submit').click()
                getEle(driver, By.CLASS_NAME, 'visible_token').text
                token = getEle(driver, By.CLASS_NAME, 'visible_token').text
            res = RESULT_OK
            msg = MSG_OK
        except Exception as err:
            token = None
            res = RESULT_ERROR
            msg = err.__str__()
            logging.error(f'[/getElearningToken]{traceback.format_exc()}')
        finally:
            driver.close()

        return json.dumps({
            'result': res,
            'msg': msg,
            'token': token
        }, ensure_ascii=False)


@app.route("/changeColor", methods=['POST'])
def changeColor():
    if request.method == 'POST':
        try:
            dic = json.loads(request.data)
            header = {'Authorization': 'Bearer ' + dic['token']}
            course_id = dic['course_id']
            color = dic['color']

            res = requests.put(ELEARNING + "/api/v1/users/self/colors/"+course_id, headers=header, data={
                "hexcode": color
            })
        except Exception:
            logging.error(f'[/changeColor]{traceback.format_exc()}')

        return json.dumps(RESULT_OK if res.status_code // 100 == 2 else RESULT_ERROR)


def getColor(header):
    # 获取颜色
    try:
        res = requests.get(ELEARNING + "/api/v1/users/self/colors", headers=header)
        return res.json()['custom_colors']
    except Exception:
        logging.error(f'[/getColor]{traceback.format_exc()}')
        return None


def getCourseList(header, color_dict):
    # 获取课程信息
    logging.error(f'\n[color_dict]\n{color_dict}')
    course_dicts = dict()
    idx = 1
    try:
        while True:
            res = requests.get(ELEARNING + "/api/v1/courses", headers=header, params={
                "enrollment_state": "active",
                "page": idx,
            })
            res_list = res.json()
            for course_dict in res_list:
                course_id = course_dict['id']
                entire_name = course_dict['original_name'] if 'original_name' in course_dict else course_dict['name']
                temp = entire_name.split(' ', 2)
                if len(temp) >= 3:
                    course_name = temp[1]
                    course_subname = temp[0] + ' ' + temp[2]
                else:
                    course_name = entire_name
                    course_subname = ''
                course_dicts[course_id] = {
                    'course_name': course_name,
                    'course_subname': course_subname,
                    'course_color': color_dict.get('course_' + str(course_id), "#4e5154") if color_dict is not None else "#4e5154",
                }
            idx += 1
            if len(res_list) == 0:
                break
        logging.error(f'\n[course_dicts]\n{course_dicts}')
    except Exception:
        logging.error(f'[/getCourseList]{traceback.format_exc()}')

    return course_dicts


def getCalendarColor(header):
    # 获取calender信息
    today = datetime.datetime.now().strftime('%Y-%m-%d')
    color_dicts = dict()
    idx = 1
    try:
        while True:
            res = requests.get(ELEARNING + "/api/v1/users/self/calendar_events", headers=header, params={
                "all_events": "true",
                'page': idx
            })
            res_list = res.json()
            for task_list in res_list:
                date = task_list['all_day_date']
                state = task_list['location_name']
                if date is None:  # 跳过无限期任务
                    continue
                if date < today:
                    if state == 'unfinish':
                        color_dicts[date] = 'unfinish'
                    elif date not in color_dicts:
                        color_dicts[date] = 'all_finish'
                elif date == today:
                    color_dicts[date] = 'today'
                else:
                    color_dicts[date] = 'future'
            idx += 1
            if len(res_list) == 0:
                break
    except Exception:
        logging.error(f'[/getCalendarColor]{traceback.format_exc()}')

    return color_dicts


def getProfile(header, color_dict):
    # 返回个人信息
    try:
        res = requests.get(ELEARNING + "/api/v1/users/self/profile", headers=header)
        res_dict = res.json()
        profile_dict = {
            'profile': {
                'avatar': res_dict['avatar_url'],
                'name': res_dict['name'],
                'student_id': res_dict['sortable_name'],
                'color': color_dict['user_' + str(res_dict['id'])] if color_dict is not None else "#4e5154",
                'user_id': str(res_dict['id'])
            },
            'user_id': str(res_dict['id'])
        }
    except Exception:
        logging.error(f'[/getProfile]{traceback.format_exc()}')

    return profile_dict


@app.route('/getMainData', methods=['POST'])
def getMainData():
    if request.method == 'POST':
        try:
            dic = json.loads(request.data)
            header = {'Authorization': 'Bearer ' + dic['token']}

            color_dict = getColor(header)
            course_dicts = getCourseList(header, color_dict)
            color_dicts = getCalendarColor(header)
            profile_dict = getProfile(header, color_dict)
            res_dict = {
                'dash_data': course_dicts,
                'calendar_data': color_dicts
            }
            res_dict.update(profile_dict)
        except Exception:
            logging.error(f'[/getMainData]{traceback.format_exc()}')

        return json.dumps(res_dict, ensure_ascii=False)


@app.route('/getCourseAnnounces', methods=['POST'])
def getCourseAnnounces():
    if request.method == 'POST':
        try:
            dic = json.loads(request.data)
            header = {'Authorization': 'Bearer ' + dic['token']}
            course_id = dic['course_id']
            start_day = "2019-01-01"
            end_day = datetime.datetime.now().strftime('%Y-%m-%d')

            # 获取课程公告
            anno_lists = list()
            idx = 1
            while True:
                res = requests.get(ELEARNING + "/api/v1/announcements", headers=header, params={
                    "context_codes": "course_"+course_id,
                    "start_date": start_day,
                    "end_date": end_day,
                    "page": idx
                })
                res_list = res.json()
                for anno_dict in res_list:
                    anno_lists.append({
                        'title': anno_dict['title'],
                        'time': anno_dict.get('posted_at', ''),
                        'author': anno_dict.get('user_name', ''),
                        'content': anno_dict.get('message', '')
                    })
                idx += 1
                if len(res_list) == 0:
                    break
        except Exception:
            logging.error(f'[/getCourseAnnounces]{traceback.format_exc()}')

        return json.dumps(anno_lists, ensure_ascii=False)


@app.route('/openFolder', methods=['POST'])
def openFolder():
    if request.method == 'POST':
        try:
            dic = json.loads(request.data)
            header = {'Authorization': 'Bearer ' + dic['token']}
            is_root = dic['is_root']

            if is_root:
                if dic['is_user']:
                    path = "users/" + dic['id']
                else:
                    path = "courses/" + dic['id']
                res = requests.get(ELEARNING + "/api/v1/" + path + "/folders/root", headers=header).json()
                files_url = res['files_url']
                folders_url = res['folders_url']
            else:
                files_url = dic['files_url']
                folders_url = dic['folders_url']

            res_list = list()
            # 文件夹
            res = requests.get(folders_url, headers=header).json()
            for folder_dict in res:
                res_list.append({
                    'type': 'folder',
                    'name': folder_dict['name'],
                    'files_url': folder_dict['files_url'],
                    'folders_url': folder_dict['folders_url']
                })
            # 文件
            res = requests.get(files_url, headers=header).json()
            for file_dict in res:
                res_list.append({
                    'type': file_dict['mime_class'],
                    'name': file_dict['display_name'],
                    'size': file_dict['size'],
                    'download_url': file_dict['url']
                })
        except Exception:
            logging.error(f'[/openFolder]{traceback.format_exc()}')

        return json.dumps(res_list, ensure_ascii=False)


@app.route('/getCourseHomework', methods=['POST'])
def getCourseHomework():
    if request.method == 'POST':
        try:
            dic = json.loads(request.data)
            header = {'Authorization': 'Bearer ' + dic['token']}
            course_id = dic['course_id']

            # 获取该课程未完成的id列表
            undo_lists = list()
            res = requests.get(ELEARNING + "/api/v1/users/self/todo", headers=header)
            res_list = res.json()
            for task in res_list:
                if "assignment" in task:
                    assignment = task['assignment']
                    if str(assignment['course_id']) == course_id:
                        undo_lists.append(assignment['id'])

            # 获取课程作业
            res = requests.get(ELEARNING + "/api/v1/courses/"+course_id+"/assignments", headers=header)
            res_list = res.json()

            hw_lists = list()
            for hw in res_list:
                if not hw.get('is_quiz_assignment', False):
                    hw_lists.append({
                        'name': hw['name'],
                        'can_dup': hw.get('can_duplicate', False),
                        'startTime': str(hw.get('created_at', '--')).replace('T', ' ').replace('Z', ''),
                        'deadline': str(hw.get('due_at', '--')).replace('T', ' ').replace('Z', ''),
                        'description': hw.get('description', ''),
                        'finished': True if hw['id'] not in undo_lists else False
                    })
        except Exception:
            logging.error(f'[/getCourseHomework]{traceback.format_exc()}')

        return json.dumps(hw_lists, ensure_ascii=False)


@app.route('/getCourseMember', methods=['POST'])
def getCourseMember():
    if request.method == 'POST':
        try:
            dic = json.loads(request.data)
            header = {'Authorization': 'Bearer ' + dic['token']}
            course_id = dic['course_id']

            member_lists = list()
            require_list = ['teacher', 'ta', 'student']
            for role in require_list:
                idx = 1
                while True:
                    res = requests.get(ELEARNING + "/api/v1/courses/"+course_id+"/search_users", headers=header, params={
                        'include[]': 'avatar_url',
                        'enrollment_type[]': role,
                        'page': idx,
                    })
                    res_list = res.json()
                    for member_dict in res_list:
                        member_lists.append({
                            'id': member_dict['id'],
                            'name': member_dict['name'],
                            'uis_id': member_dict['sortable_name'],
                            'role': role,
                            'img': member_dict['avatar_url']
                        })
                    idx += 1
                    if len(res_list) == 0:
                        break
        except Exception:
            logging.error(f'[/getCourseMember]{traceback.format_exc()}')

        return json.dumps(member_lists, ensure_ascii=False)


@app.route('/getTaskList', methods=['POST'])
def getTaskList():
    # 返回某一天的任务列表
    if request.method == 'POST':
        try:
            dic = json.loads(request.data)
            header = {'Authorization': 'Bearer ' + dic['token']}
            date = dic['date']

            unfinish_lists = list()
            finish_lists = list()
            idx = 1
            while True:
                res = requests.get(ELEARNING + "/api/v1/users/self/calendar_events", headers=header, params={
                    "start_date": date,
                    "end_date": date,
                    "page": idx
                })
                res_list = res.json()
                for task in res_list:
                    if task.get('location_name', 'unfinish') == 'finish':
                        finish_lists.append({
                            'content': task.get('title', ''),
                            'is_finished': True,
                            'task_id': task['id']
                        })
                    else:
                        unfinish_lists.append({
                            'content': task.get('title', ''),
                            'is_finished': False,
                            'task_id': task['id']
                        })
                idx += 1
                if len(res_list) == 0:
                    break
        except Exception:
            logging.error(f'[/getTaskList]{traceback.format_exc()}')

        return json.dumps(unfinish_lists + finish_lists, ensure_ascii=False)


@app.route('/postNewTask', methods=['POST'])
def postNewTask():
    # 新增任务
    if request.method == 'POST':
        try:
            dic = json.loads(request.data)
            header = {'Authorization': 'Bearer ' + dic['token']}
            date = dic['date']
            content = dic['content']
            user_id = dic['user_id']

            res = requests.post(ELEARNING + "/api/v1/calendar_events.json", headers=header, data={
                "calendar_event[context_code]": "user_"+user_id,
                "calendar_event[start_at]": date,
                "calendar_event[end_at]": date,
                "calendar_event[all_day]": True,
                "calendar_event[title]": content,
                "calendar_event[location_name]": "unfinish"
            })
        except Exception:
            logging.error(f'[/postNewTask]{traceback.format_exc()}')

        return json.dumps(RESULT_OK if res.status_code // 100 == 2 else RESULT_ERROR)


@app.route('/deleteTask', methods=['POST'])
def deleteTask():
    # 删除指定任务
    if request.method == 'POST':
        try:
            dic = json.loads(request.data)
            header = {'Authorization': 'Bearer ' + dic['token']}
            task_id = str(dic['task_id'])

            res = requests.delete(ELEARNING + "/api/v1/calendar_events/" + task_id, headers=header)

        except Exception:
            logging.error(f'[/deleteTask]{traceback.format_exc()}')

        return json.dumps(RESULT_OK if res.status_code // 100 == 2 else RESULT_ERROR)


@app.route('/finishTask', methods=['POST'])
def finishTask():
    # 标记指定任务为完成状态
    if request.method == 'POST':
        try:
            dic = json.loads(request.data)
            header = {'Authorization': 'Bearer ' + dic['token']}
            task_id = str(dic['task_id'])

            res = requests.put(ELEARNING + "/api/v1/calendar_events/" + task_id, headers=header, data={
                'calendar_event[location_name]': 'finish'
            })
        except Exception:
            logging.error(f'[/finishTask]{traceback.format_exc()}')

        return json.dumps(RESULT_OK if res.status_code // 100 == 2 else RESULT_ERROR)


@app.route('/getAllTasks', methods=['POST'])
def getAllTasks():
    if request.method == 'POST':
        try:
            dic = json.loads(request.data)
            header = {'Authorization': 'Bearer ' + dic['token']}
            # 获取课程id-name
            course_dicts = dict()
            idx = 1
            while True:
                res = requests.get(ELEARNING + "/api/v1/courses", headers=header, params={
                    "enrollment_state": "active",
                    "page": idx,
                })
                res_list = res.json()
                for course_dict in res_list:
                    entire_name = course_dict['original_name'] if 'original_name' in course_dict else course_dict['name']
                    temp = entire_name.split(' ', 2)
                    course_dicts[course_dict['id']] = entire_name if len(temp) < 3 else temp[1]
                idx += 1
                if len(res_list) == 0:
                    break

            undo_lists = list()
            # 获取未完成作业
            res = requests.get(ELEARNING + "/api/v1/users/self/todo", headers=header)
            res_list = res.json()
            for task in res_list:
                if "assignment" in task:
                    assignment = task['assignment']
                    undo_lists.append({
                        'type': 'homework',
                        'title': assignment['name'],
                        'subtitle': course_dicts.get(assignment['course_id'], ""),
                        'deadline': assignment['due_at'],
                    })

            # 获取没完成的个人task
            idx = 1
            today = datetime.datetime.now().strftime('%Y-%m-%d')
            while True:
                res = requests.get(ELEARNING + "/api/v1/users/self/calendar_events", headers=header, params={
                    "all_events": "true",
                    'page': idx
                })
                res_list = res.json()
                for task in res_list:
                    date = task.get('all_day_date', None)
                    if date is not None and (date > today or (date == today and task.get('location_name', 'unfinish') == 'unfinish')):
                        undo_lists.append({
                            'type': 'calendar',
                            'title': task.get('title', ''),
                            'subtitle': "Personal plan",
                            'deadline': date,
                        })
                idx += 1
                if len(res_list) == 0:
                    break
        except Exception:
            logging.error(f'[/getAllTasks]{traceback.format_exc()}')

        return json.dumps(undo_lists, ensure_ascii=False)


if __name__ == '__main__':
    CORS(app, support_credentials=True)
    app.run('0.0.0.0', port=5555, threaded=False)
