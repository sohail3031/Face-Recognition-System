from django.contrib import admin
from .models import Image, Employee, Admin, Worker

# class ImageAdmin(admin.ModelAdmin):
admin.site.register(Image)
admin.site.register(Employee)
admin.site.register(Admin)
admin.site.register(Worker)