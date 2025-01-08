from keras.models import load_model
from keras import backend as K
from django.core.files.storage import FileSystemStorage
from django.http import HttpResponse
from django.shortcuts import render, redirect
from pyrebase.pyrebase import Firebase
from .models import *
from .forms import *
from faceApp.utilities import *
from django.contrib import auth
import pyrebase
import os
from django.views.decorators.cache import cache_control
from django.shortcuts import get_object_or_404
from faceApp.models import Worker

#
# config = {
#     "apiKey": "AIzaSyBZW6s2OKHfWNookokwS7bB4o1Ez1gy3qU",
#     "authDomain": "face-recognition-system-57594.firebaseapp.com",
#     "databaseURL": "https://face-recognition-system-57594.firebaseio.com",
#     "projectId": "face-recognition-system-57594",
#     "storageBucket": "face-recognition-system-57594.appspot.com",
#     "messagingSenderId": "224678141322",
#     "appId": "1:224678141322:web:32addb3774886328fe3c73",
#     "measurementId": "G-QMC8S8ZKJH"
# }
config = {
    "apiKey": "AIzaSyBZW6s2OKHfWNookokwS7bB4o1Ez1gy3qU",
    "authDomain": "face-recognition-system-57594.firebaseapp.com",
    "databaseURL": "https://face-recognition-system-57594.firebaseio.com",
    "projectId": "face-recognition-system-57594",
    "storageBucket": "face-recognition-system-57594.appspot.com",
    "messagingSenderId": "224678141322",
    "appId": "1:224678141322:web:32addb3774886328fe3c73",
    "measurementId": "G-QMC8S8ZKJH"
}

firebase = pyrebase.initialize_app(config)
authentication = firebase.auth()
database = firebase.database()
storage = firebase.storage()


def startUp(request):
    return render(request, "startUp.html")


def check(request):
    if request.method == 'POST':
        choice = request.POST.get('choice')
        if choice == 'employee':
            return render(request, "logIn.html")
        elif choice == 'admin':
            return render(request, "adminLogIn.html")
    return render(request, "startUp.html")


def validateDetailsAdmin(request):
    if request.method == "POST":
        username = request.POST.get("username")
        password = request.POST.get("password")
        if username and password:
            try:
                # user = authentication.sign_in_with_email_and_password(username, password)
                # uid = user['localId']
                # session_id = user['idToken']
                # session_uid = uid
                # request.session['idToken'] = str(session_id)
                # request.session['uid'] = session_uid
                # request.session['email'] = username.split('@')[0]
                (Admin.objects.get(username=username, password=password))
                return render(request, "adminHome.html")
            except:
                message = "Username or Password is Wrong"
                return render(request, "adminLogIn.html", {'message': message})
        else:
            message = "username or password is Empty"
            return render(request, "adminLogIn.html", {'message': message})
    return render(request, "index.html")


def validateDetailsEmployee(request):
    if request.method == "POST":
        email = request.POST.get("email")
        email = email.rstrip()
        password = request.POST.get("password")
        password = password.rstrip()
        try:
            Employee.objects.get(password=password)
            try:
                authentication.sign_in_with_email_and_password(email, password)
                return render(request, "index.html")
            except:
                message = "Username or Password is Wrong"
                return render(request, "logIn.html", {'message': message})
        except:
            message = 'Please Generate Password First'
            # message = "Username or Password is Wrong"
            return render(request, "logIn.html", {'message': message})
    return render(request, "logIn.html")


def adminHomePage(request):
    return render(request, "adminHome.html")


def navigate(request):
    if request.POST.get('exitByGenerateEmployee'):
        return render(request, 'logIn.html')
    if request.POST.get('exitByForgotEmployee'):
        return render(request, 'logIn.html')
    return render(request, "startUp.html")


def logOutAdmin(request):
    return render(request, "adminLogIn.html")


def editEmployee(request):
    emp = Employee.objects.all()
    if len(emp) <= 0:
        message = "Employees Database is Empty"
        return render(request, "adminHome.html", {'message': message})
    return render(request, "editEmployee.html")


def searchByEmail(request):
    if request.method == "POST":
        email = request.POST.get('searchEmail')
        if email == "":
            return render(request, "editEmployee.html")
        try:
            employee = Employee.objects.get(email=email)
            employee = (0, employee)
            dict = {
                # change
                "message": "",
                "employee": employee
            }
            return render(request, "editEmployee.html", {'dict': dict})
        except:
            message = "Email does not exist"
            return render(request, "editEmployee.html", {'message': message})
    message = ""
    return render(request, "editEmployee.html", {'message': message})


def updateByAdmin(request):
    if request.POST.get("save"):
        empid = (request.POST.get("empid"))
        mail = (request.POST.get("email")).rstrip()
        mobileNumber = request.POST.get("mobileNumber")
        date = request.POST.get("date")
        id = int(request.POST.get('id').rstrip())
        print(id)
        try:
            mail = mail.lower().rstrip()
            employeeObject = Employee.objects.get(email=mail)
            print(employeeObject)
            data = {"empid": empid, "date": date, "mobile": mobileNumber, 'email': mail}
            database.child('users').child('Admin').child('Employee' + str(id)).update(data)
            employeeObject.empid = empid
            employeeObject.save()
            updateMobileNumberEmployee(mail, mobileNumber)
            updateDateEmployee(mail, date)
            employee = Employee.objects.all()
            employee = enumerate(employee)
            dict = {
                # change
                "message": "Updated Successfully",
                "employee": employee
            }
            return render(request, "displayAllEmployees.html", {'dict': dict})
        except:
            employee = Employee.objects.get(email=mail)
            employee = (0, employee)
            dict = {
                # change
                "message": "Cannot save, please try after some time",
                "employee": employee
            }
            return render(request, "displayAllEmployees.html", {'dict': dict})
    if request.POST.get("saveEdit"):
        empid = (request.POST.get("empid"))
        mail = (request.POST.get("email")).rstrip()
        mobileNumber = request.POST.get("mobileNumber")
        date = request.POST.get("date")
        id = int(request.POST.get('id').rstrip())
        try:
            mail = mail.lower().rstrip()
            employeeObject = Employee.objects.get(email=mail)
            print(employeeObject)
            data = {"empid": empid, "date": date, "mobile": mobileNumber, 'email': mail}
            database.child('users').child('Admin').child('Employee' + str(id)).update(data)
            employeeObject.empid = empid
            employeeObject.save()
            updateMobileNumberEmployee(mail, mobileNumber)
            updateDateEmployee(mail, date)
            employee = Employee.objects.get(email=mail)
            employee = (0, employee)
            dict = {
                # change
                "message": "Updated Successfully",
                "employee": employee
            }
            return render(request, "editEmployee.html", {'dict': dict})
        except:
            employee = Employee.objects.get(email=mail)
            employee = (0, employee)
            dict = {
                # change
                "message": "Cannot save, please try after some time",
                "employee": employee
            }
            return render(request, "editEmployee.html", {'dict': dict})
    if request.POST.get("saveWorker"):
        workerId = (request.POST.get("workerId"))
        mail = (request.POST.get("email")).rstrip()
        mobileNumber = request.POST.get("mobileNumber")
        date = request.POST.get("date")
        id = int(request.POST.get('id').rstrip())
        try:
            from .models import Worker
            mail = mail.lower().rstrip()
            workers = Worker.objects.get(email=mail)
            data = {"workerId": workerId, "date": date, "mobile": mobileNumber, 'email': mail}
            database.child('users').child('Workers').child('Worker' + str(id)).update(data)
            workers.empid = workerId
            workers.save()
            updateMobileNumberWorker(mail, mobileNumber)
            updateDateWorker(mail, date)
            workers = Worker.objects.all()
            workers = enumerate(workers)
            dict = {
                # change
                "message": "Updated Successfully",
                "employee": workers
            }
            return render(request, "displayAllWorkers.html", {'dict': dict})
        except:
            workers = Worker.objects.all()
            workers = enumerate(workers)
            dict = {
                # change
                "message": "Cannot save, please try after some time",
                "employee": workers
            }
            return render(request, "displayAllWorkers.html", {'dict': dict})
    if request.POST.get("saveEditWorker"):
        workerId = (request.POST.get("workerId"))
        mail = (request.POST.get("email")).rstrip()
        mobileNumber = request.POST.get("mobileNumber")
        date = request.POST.get("date")
        id = int(request.POST.get('id').rstrip())
        try:
            from .models import Worker
            mail = mail.lower().rstrip()
            workers = Worker.objects.get(email=mail)
            data = {"workerId": workerId, "date": date, "mobile": mobileNumber, 'email': mail}
            database.child('users').child('Workers').child('Worker' + str(id)).update(data)
            workers.empid = workerId
            workers.save()
            updateMobileNumberWorker(mail, mobileNumber)
            updateDateWorker(mail, date)
            workers = Worker.objects.get(email=mail)
            workers = (0, workers)
            dict = {
                # change
                "message": "Updated Successfully",
                "employee": workers
            }
            return render(request, "editWorkerByAdmin.html", {'dict': dict})
        except:
            workers = Worker.objects.get(email=mail)
            workers = (0, workers)
            dict = {
                # change
                "message": "Cannot save, please try after some time",
                "employee": workers
            }
            return render(request, "editWorkerByAdmin.html", {'dict': dict})
    if request.POST.get('saveEditEmployee'):
        workerId = (request.POST.get("workerId"))
        mail = (request.POST.get("email")).rstrip()
        mobileNumber = request.POST.get("mobileNumber")
        date = request.POST.get("date")
        id = int(request.POST.get('id').rstrip())
        try:
            from .models import Worker
            mail = mail.lower().rstrip()
            workers = Worker.objects.get(email=mail)
            data = {"workerId": workerId, "date": date, "mobile": mobileNumber, 'email': mail}
            database.child('users').child('Workers').child('Worker' + str(id)).update(data)
            workers.empid = workerId
            workers.save()
            updateMobileNumberWorker(mail, mobileNumber)
            updateDateWorker(mail, date)
            workers = Worker.objects.get(email=mail)
            workers = (0, workers)
            dict = {
                # change
                "message": "Updated Successfully",
                "employee": workers
            }
            return render(request, "editWorkerByEmployee.html", {'dict': dict})
        except:
            workers = Worker.objects.get(email=mail)
            workers = (0, workers)
            dict = {
                # change
                "message": "Cannot save, please try after some time",
                "employee": workers
            }
            return render(request, "editWorkerByEmployee.html", {'dict': dict})
    if request.POST.get('saveAllEmployee'):
        workerId = (request.POST.get("workerId"))
        mail = (request.POST.get("email")).rstrip()
        mobileNumber = request.POST.get("mobileNumber")
        date = request.POST.get("date")
        id = int(request.POST.get('id').rstrip())
        try:
            from .models import Worker
            mail = mail.lower().rstrip()
            workers = Worker.objects.get(email=mail)
            data = {"workerId": workerId, "date": date, "mobile": mobileNumber, 'email': mail}
            database.child('users').child('Workers').child('Worker' + str(id)).update(data)
            workers.empid = workerId
            workers.save()
            updateMobileNumberWorker(mail, mobileNumber)
            updateDateWorker(mail, date)
            workers = Worker.objects.get(email=mail)
            workers = enumerate(workers)
            dict = {
                # change
                "message": "Updated Successfully",
                "employee": workers
            }
            return render(request, "editWorkerByEmployee.html", {'dict': dict})
        except:
            workers = Worker.objects.get(email=mail)
            workers = enumerate(workers)
            dict = {
                # change
                "message": "Cannot save, please try after some time",
                "employee": workers
            }
            return render(request, "editWorkerByEmployee.html", {'dict': dict})
    if request.POST.get('deleteEditEmployee'):
        id = int(request.POST.get('id').rstrip())
        try:
            from .models import Worker
            database.child('users').child('Workers').child('Worker' + str(id)).remove()
            Worker.objects.filter(customId=id).delete()
            workers = Worker.objects.all()
            workers = (0, workers)
            dict = {
                # change
                "message": "Deleted Successfully",
                "employee": workers
            }
            return render(request, "editWorkerByEmployee.html", {'dict': dict})
        except:
            workers = Worker.objects.all()
            workers = (0, workers)
            dict = {
                # change
                "message": "Cannot delete Employee data",
                "employee": workers
            }
            return render(request, "editWorkerByEmployee.html", {'dict': dict})
    if request.POST.get('deleteAllEmployee'):
        id = int(request.POST.get('id').rstrip())
        try:
            from .models import Worker
            database.child('users').child('Workers').child('Worker' + str(id)).remove()
            Worker.objects.filter(customId=id).delete()
            workers = Worker.objects.all()
            workers = enumerate(workers)
            dict = {
                # change
                "message": "Deleted Successfully",
                "employee": workers
            }
            return render(request, "editWorkerByEmployee.html", {'dict': dict})
        except:
            workers = Worker.objects.all()
            workers = enumerate(workers)
            dict = {
                # change
                "message": "Cannot delete Employee data",
                "employee": workers
            }
            return render(request, "editWorkerByEmployee.html", {'dict': dict})
    if request.POST.get("deleteWorker"):
        workerId = (request.POST.get("workerId"))
        mail = (request.POST.get("email")).rstrip()
        mobileNumber = request.POST.get("mobileNumber")
        date = request.POST.get("date")
        id = int(request.POST.get('id').rstrip())
        try:
            from .models import Worker
            delete = str('Employee' + str(id))
            database.child('users').child('Workers').child('Worker' + str(id)).remove()
            Worker.objects.filter(customId=id).delete()
            workers = Worker.objects.all()
            workers = enumerate(workers)
            dict = {
                # change
                "message": "Deleted Successfully",
                "employee": workers
            }
            return render(request, "displayAllWorkers.html", {'dict': dict})
        except:
            workers = Employee.objects.all()
            workers = enumerate(workers)
            dict = {
                # change
                "message": "Cannot delete Employee data",
                "employee": workers
            }
            return render(request, "displayAllWorkers.html", {'dict': dict})
    if request.POST.get("deleteEditWorker"):
        id = int(request.POST.get('id').rstrip())
        try:
            from .models import Worker
            database.child('users').child('Workers').child('Worker' + str(id)).remove()
            Worker.objects.filter(customId=id).delete()
            workers = Worker.objects.all()
            workers = (0, workers)
            dict = {
                # change
                "message": "Deleted Successfully",
                "employee": workers
            }
            return render(request, "editWorkerByAdmin.html", {'dict': dict})
        except:
            workers = Worker.objects.all()
            workers = (0, workers)
            dict = {
                # change
                "message": "Cannot delete Employee data",
                "employee": workers
            }
            return render(request, "editWorkerByAdmin.html", {'dict': dict})
    if request.POST.get("deleteEdit"):
        empid = (request.POST.get("empid"))
        mail = (request.POST.get("email")).rstrip()
        mobileNumber = request.POST.get("mobileNumber")
        date = request.POST.get("date")
        id = int(request.POST.get('id').rstrip())
        try:
            delete = str('Employee' + str(id))
            database.child('users').child('Employees').child(delete).remove()
            Employee.objects.filter(customId=id).delete()
            message = "Employee Data deleted successfully"
            return render(request, "editEmployee.html", {'message': message})
        except:
            employee = Employee.objects.get(email=mail)
            employee = (0, employee)
            dict = {
                # change
                "message": "Cannot delete Employee data",
                "employee": employee
            }
            return render(request, "editEmployee.html", {'dict': dict})
    if request.POST.get("delete"):
        empid = (request.POST.get("empid"))
        mail = (request.POST.get("email")).rstrip()
        mobileNumber = request.POST.get("mobileNumber")
        date = request.POST.get("date")
        id = int(request.POST.get('id').rstrip())
        try:
            delete = str('Employee' + str(id))
            database.child('users').child('Employees').child(delete).remove()
            Employee.objects.filter(customId=id).delete()
            employee = Employee.objects.all()
            employee = enumerate(employee)
            dict = {
                # change
                "message": "Employee Data deleted successfully",
                "employee": employee
            }
            return render(request, "displayAllEmployees.html", {'dict': dict})
        except:
            employee = Employee.objects.all()
            employee = enumerate(employee)
            dict = {
                # change
                "message": "Cannot delete Employee data",
                "employee": employee
            }
            return render(request, "displayAllEmployees.html", {'dict': dict})
    employee = Employee.objects.all()
    employee = enumerate(employee)
    dict = {
        # change
        "message": "Updated Successfully",
        "employee": employee
    }
    return render(request, "displayAllEmployees.html", {'dict': dict})


def updateMobileNumberEmployee(email, mobileNumber):
    employeeObject = Employee.objects.get(email=email)
    employeeObject.mobileNumber = mobileNumber
    employeeObject.save()


def updateDateEmployee(email, date):
    employeeObject = Employee.objects.get(email=email)
    employeeObject.date = date
    employeeObject.save()


def updateMobileNumberWorker(email, mobileNumber):
    employeeObject = Worker.objects.get(email=email)
    employeeObject.mobileNumber = mobileNumber
    employeeObject.save()


def updateDateWorker(email, date):
    employeeObject = Worker.objects.get(email=email)
    employeeObject.date = date
    employeeObject.save()


def addWorkerByAdmin(request):
    from django.db.models import Max
    id = Worker.objects.all().aggregate(Max('customId'))
    id = id['customId__max']
    if not id:
        id = 1
    else:
        id = id + 1
    workerId = (request.POST.get('workerId'))
    email = str(request.POST.get('email'))
    email = email.lower().rstrip()
    mobile = request.POST.get('mobileNo')
    date = request.POST.get('date')
    password = ""
    idToken = request.session['idToken']
    uid = request.session['uid']
    db = firebase.database()
    privateKey = request.session['email']
    employeeId = str('Worker' + str(id))
    if request.POST.get('createAccountEmployee'):
        value = False
        try:
            value = Worker.objects.get(email=email)
            print(value)
        except:
            value: False
        if not value:
            # try:
            data = {'workerId': workerId, 'email': email, 'password': password, 'mobile': mobile,
                    'date': date}
            database.child('users').child('Workers').child(employeeId).set(data)
            Worker.objects.create(workerId=workerId, email=email, password=password, mobileNumber=mobile,
                                  date=date, customId=id)
            message = "Registered Successfully"
            return render(request, "addWorkerByEmployee.html", {'message': message})
            # except:
            #     message = "Account cannot be Created"
            #     return render(request, "addWorkerByAdmin.html", {'message': message})
        else:
            message = "Email Already Exists"
            return render(request, "addWorkerByEmployee.html", {'message': message})
    if request.method == "POST":
        value = False
        try:
            value = Worker.objects.get(email=email)
            print(value)
        except:
            value: False
        if not value:
            # try:
            data = {'workerId': workerId, 'email': email, 'password': password, 'mobile': mobile,
                    'date': date}
            database.child('users').child('Workers').child(employeeId).set(data)
            Worker.objects.create(workerId=workerId, email=email, password=password, mobileNumber=mobile,
                                  date=date, customId=id)
            message = "Registered Successfully"
            return render(request, "addWorkerByAdmin.html", {'message': message})
            # except:
            #     message = "Account cannot be Created"
            #     return render(request, "addWorkerByAdmin.html", {'message': message})
        else:
            message = "Email Already Exists"
            return render(request, "addWorkerByAdmin.html", {'message': message})
    return render(request, "addWorkerByAdmin.html")


def editWorkerByAdmin(request):
    worker = Worker.objects.all()
    if len(worker) <= 0:
        message = "Workers Database is Empty"
        return render(request, "adminHome.html", {'message': message})
    else:
        return render(request, "editWorkerByAdmin.html")


def searchWorkerByEmail(request):
    if request.POST.get('searchEmailEmployee'):
        email = request.POST.get('searchEmail')
        email = email.rstrip()
        if email == "":
            return render(request, "editWorkerByEmployee.html")
        try:
            worker = Worker.objects.get(email=email)
            worker = (0, worker)
            dict = {
                # change
                "message": "",
                "employee": worker
            }
            return render(request, "editWorkerByEmployee.html", {'dict': dict})
        except:
            message = "Email does not exist"
            return render(request, "editWorkerByEmployee.html", {'message': message})
    if request.method == "POST":
        email = request.POST.get('searchEmail')
        email = email.rstrip()
        if email == "":
            return render(request, "editWorkerByAdmin.html")
        try:
            worker = Worker.objects.get(email=email)
            worker = (0, worker)
            dict = {
                # change
                "message": "",
                "employee": worker
            }
            return render(request, "editWorkerByAdmin.html", {'dict': dict})
        except:
            message = "Email does not exist"
            return render(request, "editWorkerByAdmin.html", {'message': message})
    message = ""
    return render(request, "editEmployee.html", {'message': message})


def displayWorkerListByAdmin(request):
    workers = Worker.objects.all()
    if workers:
        workers = enumerate(workers)
        dict = {
            # change
            "message": "",
            "employee": workers
        }
        return render(request, "displayAllWorkers.html", {'dict': dict})
    else:
        message = "The Database is currently empty"
        return render(request, "adminHome.html", {'message': message})


#
#
# def updateByAdmin(request):
#     empid = (request.POST.get("empid"))
#     mail = (request.POST.get("email")).rstrip()
#     mobileNumber = request.POST.get("mobileNumber")
#     date = request.POST.get("date")
#     id = int(request.POST.get('id').rstrip())
#     if request.POST.get("save"):
#         print(id)
#         try:
#             mail = mail.lower().rstrip()
#             employeeObject = Employee.objects.get(email=mail)
#             print(employeeObject)
#             data = {"empid": empid, "date": date, "mobile": mobileNumber, 'email': mail}
#             database.child('users').child('Admin').child('Employee'+str(id)).update(data)
#             employeeObject.empid = empid
#             employeeObject.save()
#             updateMobileNumber(mail, mobileNumber)
#             updateDate(mail, date)
#             employee = Employee.objects.get(email=mail)
#             employee = (0, employee)
#             dict = {
#                 # change
#                 "message": "Updated Successfully",
#                 "employee": employee
#             }
#             return render(request, "editEmployee.html", {'dict': dict})
#         except:
#             employee = Employee.objects.get(email=mail)
#             employee = (0, employee)
#             dict = {
#                 # change
#                 "message": "Cannot save, please try after some time",
#                 "employee": employee
#             }
#             return render(request, "editEmployee.html", {'dict': dict})
#     if request.POST.get("delete"):
#         try:
#             delete = str('Employee' + str(id))
#             database.child('users').child('Admin').child(delete).remove()
#             Employee.objects.filter(customId=id).delete()
#             message = "Employee Data deleted successfully"
#             return render(request, "editEmployee.html", {'message': message})
#         except:
#             employee = Employee.objects.get(email=mail)
#             employee = (0, employee)
#             dict = {
#                 # change
#                 "message": "Cannot delete Employee data",
#                 "employee": employee
#             }
#             return render(request, "editEmployee.html", {'dict': dict})
#     return render(request, "editEmployee.html")


def updateMobileNumber(email, mobileNumber):
    employeeObject = Employee.objects.get(email=email)
    employeeObject.mobileNumber = mobileNumber
    employeeObject.save()


def updateDate(email, date):
    employeeObject = Employee.objects.get(email=email)
    employeeObject.date = date
    employeeObject.save()


def addFraudDataByAdmin(request):
    from django.db.models import Max
    id = Image.objects.all().aggregate(Max('customId'))
    id = id['customId__max']
    if not id:
        id = 1
    else:
        id = id + 1
    fraudId = str('FraudPerson' + str(id))
    fs = FileSystemStorage(location="C:\\Users\\Amair\\Documents\\faceProject\\Data\\andriod\\")
    absoluteUrl = "C:\\Users\\Amair\\Documents\\faceProject\\Data\\andriod\\"
    if request.POST.get('submitImage'):
        description = request.POST.get('description')
        loss = request.POST.get('loss')
        name = request.POST.get('name')
        name = name.lower().rstrip()
        image = request.FILES['image']
        imageName = fs.save(name, image)
        url = absoluteUrl + imageName
        numericLoss = int(loss)
        if numericLoss < 0:
            message = "Loss must be Greater than Zero"
            return render(request, "addFraudPersonByAdmin.html", {'message': message})
        elif not name:
            message = "Name field cannot be Empty"
            return render(request, "addFraudPersonByAdmin.html", {'message': message})
        try:
            data = {'name': name, 'description': description, 'loss': loss}
            database.child('users').child('FraudPersons').child(fraudId).set(data)
            storage.child('users').child('FraudPersons').child(str(fraudId)).put(url)
            Image.objects.create(name=name, description=description, loss=loss, image=image, customId=id)
            message = "Uploaded Successfully"
            print("addFraudDataByAdmin")
            return render(request, "addFraudPersonByAdmin.html", {'message': message})
        except:
            message = "Something went wrong! Please try again"
            return render(request, "addFraudPersonByAdmin.html", {'message': message})
    return render(request, "addFraudPersonByAdmin.html", {'id': id})


#
# def editFraudDataByAdmin(request):
#     image = Image.objects.all()
#     if len(image) <= 0:
#         message = "Fraud Person Database is Empty"
#         return render(request, "adminHome.html", {'message': message})
#     else:
#         return render(request, "editFraudPersonByAdmin.html")
#
#
# def searchFraudPersonByAdmin(request):
#     if request.method == "POST":
#         name = request.POST.get('searchName')
#         if name == "":
#             return render(request, "editWorkerByAdmin.html")
#         try:
#             name = name.rstrip().lower()
#             print(name)
#             print(name.replace(" ", "#"))
#             images = Image.objects.get(name=name)
#             images = (0, images)
#             dict = {
#                 # change
#                 "message": "",
#                 "images": images
#             }
#             return render(request, "editFraudPersonByAdmin.html", {'dict': dict})
#         except:
#             message = "Name does not exist"
#             return render(request, "editFraudPersonByAdmin.html", {'message': message})
#     message = ""
#     return render(request, "editFraudPersonByAdmin.html", {'message': message})


def displayFraudPersonListByAdmin(request):
    images = Image.objects.all()
    print(images)
    if images:
        images = enumerate(images)
        dict = {
            # change
            "message": "",
            "images": images
        }
        return render(request, "displayAllFraudpersons.html", {'dict': dict})
    else:
        message = "The Database is currently empty"
        return render(request, "adminHome.html", {'message': message})


@cache_control(no_cache=True, must_revalidate=True, no_store=True)
def displayImagesView(request):
    if request.method == 'GET':
        images = Image.objects.all()
        # print(type(images))
        if images:
            images = enumerate(images)
            dict = {
                # change
                "message": "",
                "images": images
            }
            return render(request, 'displayImages.html', {'dict': dict})
        else:
            message = "Database is empty"
            return render(request, 'index.html', {'message':message})
    images = Image.objects.all()
    if images:
        images = enumerate(images)
        dict = {
            # change
            "message": "",
            "images": images
        }
        return render(request, 'displayImages.html', {'dict': dict})
    else:
        message = "Database is empty"
        return render(request, 'index.html', {'message': message})


def validateDetails(request):
    email = request.POST.get('email')
    password = request.POST.get('password')
    try:
        user = authentication.sign_in_with_email_and_password(email, password)
    except:
        # print("inside except")
        message = "Invalid credential"
        return render(request, 'logIn.html', {'message': message})
    uid = user['localId']
    session_id = user['idToken']
    request.session['uid'] = str(session_id)
    return render(request, "index.html")


def indexView(request):
    if request.POST.get('btn'):
        images = Image.objects.all()
        if len(images) <= 0:
            message = "This process requires data and Database is empty"
            return render(request, "index.html", {'message': message})
        K.clear_session()
        loaded_model = load_model('C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\model\\facenet_keras.h5')
        # print("model loaded")
        loadStaticEmbedding(loaded_model, request)
        loadEmbeddings(loaded_model)
    return render(request, "index.html")


def logInView(request):
    return render(request, "logIn.html")


def logOutView(request):
    try:
        del request.session['uid']
    except KeyError:
        pass
    return render(request, "logIn.html")


def signUpView(request):
    # if request.POST.get('createAccount'):
    from django.db.models import Max
    id = Employee.objects.all().aggregate(Max('customId'))
    id = id['customId__max']
    if not id:
        # print("if")
        id = 1
    else:
        # print("else")
        id = id + 1
    empid = request.POST.get('empid')
    email = str(request.POST.get('email'))
    email = email.lower().rstrip()
    mobile = request.POST.get('mobileNo')
    date = request.POST.get('date')
    password = ""
    privateKey = request.session['email']
    employeeId = str('Employee' + str(id))
    if request.method == "POST":
        value = False
        try:
            value = Employee.objects.get(email=email)
        except:
            value: False
        if not value:
            try:
                data = {'empid': empid, 'email': email, 'password': password, 'mobile': mobile,
                        'date': date}
                database.child('users').child('Employees').child(employeeId).set(data)
                Employee.objects.create(empid=empid, email=email, password=password, mobileNumber=mobile,
                                        date=date, customId=id)
                message = "Registered Successfully"
                return render(request, "signUp.html", {'message': message})
            except:
                message = "Account cannot be Created"
                return render(request, "signUp.html", {'message': message})
        else:
            message = "Email Already Exists"
            return render(request, "signUp.html", {'message': message})
    return render(request, "signUp.html")


def displayAllEmployees(request):
    employees = Employee.objects.all()
    if employees:
        employees = enumerate(employees)
        dict = {
            # change
            "message": "",
            "employee": employees
        }
        return render(request, "displayAllEmployees.html", {'dict': dict})
    else:
        message = "The Database is currently empty"
        return render(request, "adminHome.html", {'message': message})


@cache_control(no_cache=True, must_revalidate=True, no_store=True)
def uploadView(request):
    from django.db.models import Max
    id = Image.objects.all().aggregate(Max('customId'))
    id = id['customId__max']
    if not id:
        id = 1
    else:
        id = id + 1
    fraudId = str('FraudPerson' + str(id))
    fs = FileSystemStorage(location="C:\\Users\\Amair\\Documents\\faceProject\\Data\\andriod\\")
    absoluteUrl = "C:\\Users\\Amair\\Documents\\faceProject\\Data\\andriod\\"
    if request.POST.get('submitImage'):
        description = request.POST.get('description')
        loss = request.POST.get('loss')
        name = request.POST.get('name')
        name = name.lower().rstrip()
        image = request.FILES['image']
        imageName = fs.save(name, image)
        url = absoluteUrl + imageName
        numericLoss = int(loss)
        if numericLoss < 0:
            message = "Loss must be Greater than Zero"
            return render(request, "addFraudPersonByAdmin.html", {'message': message})
        elif not name:
            message = "Name field cannot be Empty"
            return render(request, "addFraudPersonByAdmin.html", {'message': message})
        try:
            data = {'name': name, 'description': description, 'loss': loss}
            database.child('users').child('FraudPersons').child(fraudId).set(data)
            storage.child('users').child('FraudPersons').child(str(fraudId)).put(url)
            Image.objects.create(name=name, description=description, loss=loss, image=image, customId=id)
            message = "Uploaded Successfully"
            print("addFraudDataByAdmin")
            return render(request, "addFraudPersonByAdmin.html", {'message': message})
        except:
            message = "Something went wrong! Please try again"
            return render(request, "addFraudPersonByAdmin.html", {'message': message})
    return render(request, "addFraudPersonByAdmin.html", {'id': id})


def modifySerializeData(request, newKeyDic, keyDic, previous_data):
    # print("modifying started")
    id = list(newKeyDic.keys())
    # print("id: {}".format(id))
    name = keyDic[id[0]]
    name = name + "_" + str((id[0]))
    newValue = {name: previous_data[name]}
    print("Data Before printing")
    print(previous_data)
    previous_data = {key: val for key, val in previous_data.items() if key != name}
    print("Data After printing")
    print(previous_data)
    newName = (list(newKeyDic.values())[0] + "_" + str(id[0])).lower()
    previous_data[newName] = newValue[name]
    f = open("C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\model\\file.pkl", "wb")
    pickle.dump(previous_data, f)
    f.close()
    # print(previous_data)
    # print("modifying ended")
    print(previous_data)


def updateView(request):
    id = request.POST.get("id")
    name = request.POST.get("name")
    name = name.rstrip()
    description = request.POST.get("description")
    description = description.rstrip()
    loss = request.POST.get("loss")
    privateId = str('FraudPerson') + str(id)
    if request.POST.get("save"):
        numericLoss = int(loss)
        images = Image.objects.all()[:10]
        images = enumerate(images)
        if numericLoss < 0:
            dict = {
                # changes
                'message': "Loss must be Greater than Zero",
                'images': images
            }
            return render(request, "displayImages.html", {'dict': dict})
        elif not name:
            dict = {
                # changes
                'message': "Name field cannot be Empty",
                'images': images
            }
            return render(request, "displayImages.html", {'dict': dict})
        from os import path
        if not path.exists("C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\model\\file.pkl"):
            print("file does not exists")
        else:
            pickle_in = open("C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\model\\file.pkl", "rb")
            previous_data = pickle.load(pickle_in)
            print(pickle_in)
            pickle_in.close()
            list1 = list(previous_data.keys())
            print(list1)
            # print(list1)
            keyDic = {}
            newKeyDic = {}
            for item in list1:
                # print(item)
                keyDic[item.split("_")[1]] = item.split("_")[0]
            newKeyDic[str(id)] = name
            print(newKeyDic)
            print(keyDic)
            if id in keyDic.keys():
                print('modifying serialize data')
                modifySerializeData(request, newKeyDic, keyDic, previous_data)
        data = {"name": name, "description": description, "loss": loss}
        database.child('users').child('FraudPersons').child(privateId).update(data)
        imageObject = Image.objects.get(customId=id)
        imageObject.name = name
        imageObject.save()
        updateDescription(id, description)
        updateLoss(id, loss)
        message = "Updated SuccessFully"
        images = Image.objects.all()[:10]
        images = enumerate(images)
        dict = {
            'message': message,
            'images': images
        }
        return render(request, "displayImages.html", {'dict': dict})
    if request.POST.get("saveByAdmin"):
        numericLoss = int(loss)
        images = Image.objects.all()[:10]
        images = enumerate(images)
        if numericLoss < 0:
            dict = {
                # changes
                'message': "Loss must be Greater than Zero",
                'images': images
            }
            return render(request, "displayAllFraudpersons.html", {'dict': dict})
        elif not name:
            dict = {
                # changes
                'message': "Name field cannot be Empty",
                'images': images
            }
            return render(request, "displayAllFraudpersons.html", {'dict': dict})
        from os import path
        if not path.exists("C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\model\\file.pkl"):
            print("file does not exists")
        else:
            pickle_in = open("C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\model\\file.pkl", "rb")
            previous_data = pickle.load(pickle_in)
            print(pickle_in)
            pickle_in.close()
            list1 = list(previous_data.keys())
            print(list1)
            # print(list1)
            keyDic = {}
            newKeyDic = {}
            for item in list1:
                # print(item)
                keyDic[item.split("_")[1]] = item.split("_")[0]
            newKeyDic[str(id)] = name
            print(newKeyDic)
            print(keyDic)
            modifySerializeData(request, newKeyDic, keyDic, previous_data)
        data = {"name": name, "description": description, "loss": loss}
        database.child('users').child('FraudPersons').child(privateId).update(data)
        imageObject = Image.objects.get(customId=id)
        imageObject.name = name
        imageObject.save()
        updateDescription(id, description)
        updateLoss(id, loss)
        message = "Updated SuccessFully"
        images = Image.objects.all()[:10]
        images = enumerate(images)
        dict = {
            'message': message,
            'images': images
        }
        return render(request, "displayAllFraudpersons.html", {'dict': dict})
    if request.POST.get("deleteByAdmin"):
        try:
            from os import path
            if not path.exists("C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\model\\file.pkl"):
                print("file does not exists")
            else:
                pickle_in = open("C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\model\\file.pkl", "rb")
                previous_data = pickle.load(pickle_in)
                pickle_in.close()
                length = list(previous_data.keys())
                if len(length) > 1:
                    name = (name.lower().rstrip() + "_" + str(id))
                    print(name)
                    try:
                        print("trying to delete {}".format(name))
                        # del previous_data[name]
                        print(type(name))
                        new_data = {key: val for key, val in previous_data.items() if key != str(name)}
                        f = open("C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\model\\file.pkl", "wb")
                        pickle.dump(new_data, f)
                        f.close()
                        print("Data after Deleting the member")
                        print(new_data)
                    except KeyError as error:
                        print("Inside Except block")
                        # print(str(error))
                        pickle_in = open("C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\model\\file.pkl",
                                         "rb")
                        previous_data = pickle.load(pickle_in)
                        print(previous_data)
                        pickle_in.close()
                        images = Image.objects.all()[:10]
                        images = enumerate(images)
                        dict = {
                            'message': error,
                            'images': images
                        }
                        print(error.args)
                        print(error)
                        return render(request, "displayAllFraudpersons.html", {"dict": dict})
                else:
                    print(type(name))
                    os.remove("C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\model\\file.pkl")
                    print("File Removed!")

            database.child('users').child('FraudPersons').child(privateId).remove()
            Image.objects.filter(customId=id).delete()
            images = Image.objects.all()[:10]
            images = enumerate(images)
            dict = {
                'message': "Item Deleted SuccessFully!!",
                'images': images
            }
        except KeyError:
            message = "Something went wrong! Please try again"
            return render(request, "displayAllFraudpersons.html", {'message': message})
        return render(request, "displayAllFraudpersons.html", {'dict': dict})
    if request.POST.get("delete"):
        try:
            from os import path
            if not path.exists("C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\model\\file.pkl"):
                print("file does not exists")
            else:
                pickle_in = open("C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\model\\file.pkl", "rb")
                previous_data = pickle.load(pickle_in)
                pickle_in.close()
                length = list(previous_data.keys())
                if len(length) > 1:
                    name = (name.lower().rstrip() + "_" + str(id))
                    try:
                        print("trying to delete {}".format(name))
                        # del previous_data[name]
                        print(type(name))
                        new_data = {key: val for key, val in previous_data.items() if key != str(name)}
                        f = open("C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\model\\file.pkl", "wb")
                        pickle.dump(new_data, f)
                        f.close()
                        print("Data after Deleting the member")
                        print(new_data)
                    except KeyError as error:
                        print("Inside Except block")
                        # print(str(error))
                        pickle_in = open("C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\model\\file.pkl",
                                         "rb")
                        previous_data = pickle.load(pickle_in)
                        print(previous_data)
                        pickle_in.close()
                        images = Image.objects.all()[:10]
                        images = enumerate(images)
                        dict = {
                            'message': error,
                            'images': images
                        }
                        print(error.args)
                        print(error)
                        return render(request, "displayImages.html", {"dict": dict})
                else:
                    print(type(name))
                    os.remove("C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\model\\file.pkl")
                    print("File Removed!")

            database.child('users').child('FraudPersons').child(privateId).remove()
            Image.objects.filter(customId=id).delete()
            images = Image.objects.all()[:10]
            images = enumerate(images)
            dict = {
                'message': "Item Deleted SuccessFully!!",
                'images': images
            }
        except KeyError:
            message = "Something went wrong! Please try again"
            return render(request, "displayImages.html", {'message': message})
        return render(request, "displayImages.html", {'dict': dict})
    images = Image.objects.all()[:10]
    images = enumerate(images)
    dict = {
        # changes
        'message': "",
        'images': images
    }
    return render(request, "displayImages.html", {'dict': dict})


def updateLoss(id, loss):
    imageObject = Image.objects.get(customId=id)
    imageObject.loss = loss
    imageObject.save()


def updateDescription(id, description):
    imageObject = Image.objects.get(customId=id)
    imageObject.description = description
    imageObject.save()


# Generate Password by the Employee
def generatePassword(request):
    if request.POST.get('generate'):
        email = request.POST.get('email')
        email = email.rstrip()
        password = request.POST.get('password')
        rePassword = request.POST.get('rePassword')
        password = password.rstrip()
        rePassword = rePassword.rstrip()
        if email is "":
            print('email is empty')
            message = 'Please Enter proper Email ID'
            return render(request, 'generatePasswordByEmployee.html', {'message': message})
        if password == rePassword:
            # try:
            employeeObject = Employee.objects.get(email=email)
            checkPassword = employeeObject.password
            customId = employeeObject.customId
            privateId = "Employee"+str(customId)
            print(privateId)
            if not checkPassword:
                data = {'password': password}
                database.child('users').child('Employees').child(privateId).update(data)
                authentication.create_user_with_email_and_password(email, password)
                employeeObject.password = password
                employeeObject.save()
                message = 'Password is successfully generated Now LogIn'
                return render(request, 'logIn.html', {'message': message})
            else:
                message = 'Password is Already generated, if you forgot your password click on Forgot Password'
                return render(request, 'logIn.html', {'message': message})
            # except:
            #     message = 'Email does not exist'
            #     return render(request, 'generatePasswordByEmployee.html', {'message': message})
        else:
            message = 'Passwords should match'
            return render(request, 'generatePasswordByEmployee.html', {'message':message})
    return render(request, 'generatePasswordByEmployee.html')


# It deals with the forgot password of Employee page
def forgotPassword(request):
    if request.POST.get("forgetSubmit"):
        email = request.POST.get('email')
        email = email.rstrip()
        authentication.send_password_reset_email(email)
        message = 'Passwords reset link has been send to your Email'
        return render(request, 'logIn.html', {'message': message})
    return render(request, 'forgotPasswordByEmployee.html')


# add worker by Employee
def addWorkerByEmployee(request):
    return render(request, 'addWorkerByEmployee.html')


def editWorkerByEmployee(request):
    worker = Worker.objects.all()
    if len(worker) <= 0:
        message = "Workers Database is Empty"
        return render(request, "index.html", {'message': message})
    return render(request, 'editWorkerByEmployee.html')


def displayWorkerListByEmployee(request):
    workers = Worker.objects.all()
    if workers:
        workers = enumerate(workers)
        dict = {
            # change
            "message": "",
            "employee": workers
        }
        return render(request, "displayWorkerListByEmployee.html", {'dict': dict})
    else:
        message = "The Database is currently empty"
        return render(request, "index.html", {'message': message})


def addFraudDataByEmployee(request):
    from django.db.models import Max
    id = Image.objects.all().aggregate(Max('customId'))
    id = id['customId__max']
    if not id:
        id = 1
    else:
        id = id + 1
    fraudId = str('FraudPerson' + str(id))
    fs = FileSystemStorage(location="C:\\Users\\Amair\\Documents\\faceProject\\Data\\andriod\\")
    absoluteUrl = "C:\\Users\\Amair\\Documents\\faceProject\\Data\\andriod\\"
    if request.POST.get('submitImage'):
        description = request.POST.get('description')
        loss = request.POST.get('loss')
        name = request.POST.get('name')
        name = name.lower().rstrip()
        image = request.FILES['image']
        imageName = fs.save(name, image)
        url = absoluteUrl + imageName
        numericLoss = int(loss)
        if numericLoss < 0:
            message = "Loss must be Greater than Zero"
            return render(request, "form.html", {'message': message})
        elif not name:
            message = "Name field cannot be Empty"
            return render(request, "form.html", {'message': message})
        try:
            data = {'name': name, 'description': description, 'loss': loss}
            database.child('users').child('FraudPersons').child(fraudId).set(data)
            storage.child('users').child('FraudPersons').child(str(fraudId)).put(url)
            Image.objects.create(name=name, description=description, loss=loss, image=image, customId=id)
            message = "Uploaded Successfully"
            return render(request, "form.html", {'message': message})
        except:
            message = "Something went wrong! Please try again"
            return render(request, "form.html", {'message': message})
    return render(request, "form.html", {'id': id})