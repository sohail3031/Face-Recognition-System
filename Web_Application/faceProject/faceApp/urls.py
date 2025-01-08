from django.contrib import admin
from django.urls import path, include
from django.conf import settings
from django.conf.urls.static import static
from .views import *
from django.contrib.staticfiles.urls import staticfiles_urlpatterns

urlpatterns = [
    path('admin/', admin.site.urls),
    path('', startUp, name="startUp"),
    path('index', indexView, name='indexView'),
    path('image', displayImagesView, name="displayView"),
    path('logIn', logInView, name='logInView'),
    path('signUp', signUpView, name='signUpView'),
    path('logout/', logOutView, name="logOutView"),
    path('check', check, name="check"),
    path('navigate', navigate, name='navigate'),
    path('validateDetailsAdmin', validateDetailsAdmin, name="validateDetailsAdmin"),
    path('validateDetails', validateDetails, name="validateDetails"),
    path('upload', uploadView, name="uploadView"),
    path('update', updateView, name="update"),
    path('LogOut', logOutAdmin, name="logOutAdmin"),
    path('editEmployee', editEmployee, name="editEmployee"),
    path('adminHomePage', adminHomePage, name="adminHomePage"),
    path('searchByEmail', searchByEmail, name='searchByEmail'),
    path('validateDetailsEmployee', validateDetailsEmployee, name="validateDetailsEmployee"),
    path('updateByAdmin', updateByAdmin, name="updateByAdmin"),
    path('displayAllEmployees', displayAllEmployees, name='displayAllEmployees'),
    path('addWorkerByAdmin', addWorkerByAdmin, name='addWorkerByAdmin'),
    path('editWorkerByAdmin', editWorkerByAdmin, name='editWorkerByAdmin'),
    path('displayWorkerListByAdmin', displayWorkerListByAdmin, name='displayWorkerListByAdmin'),
    path('addFraudDataByAdmin', addFraudDataByAdmin, name='addFraudDataByAdmin'),
    path('displayFraudPersonListByAdmin', displayFraudPersonListByAdmin, name='displayFraudPersonListByAdmin'),
    path('searchWorkerByEmail', searchWorkerByEmail, name='searchWorkerByEmail'),
    path('generatePassword', generatePassword, name='generatePassword'),
    path('forgotPassword', forgotPassword, name='forgotPassword'),
    path('addWorkerByEmployee', addWorkerByEmployee, name='addWorkerByEmployee'),
    path('editWorkerByEmployee', editWorkerByEmployee, name='editWorkerByEmployee'),
    path('displayWorkerListByEmployee', displayWorkerListByEmployee, name='displayWorkerListByEmployee'),
    path('addFraudDataByEmployee', addFraudDataByEmployee, name='addFraudDataByEmployee')
]
if settings.DEBUG:
    urlpatterns += static(settings.STATIC_URL, document_root=settings.STATIC_ROOT)
#
if settings.DEBUG:
    urlpatterns += static(settings.MEDIA_URL,
                          document_root=settings.MEDIA_ROOT)
