from django.db import models
from django.conf import settings
from datetime import datetime


# Create your models here.
class Image(models.Model):
    customId = models.IntegerField(primary_key=True)
    name = models.CharField(max_length=100, default='Name')
    description = models.TextField(max_length=100, blank=True)
    loss = models.IntegerField(default=1000)
    image = models.ImageField(upload_to='images/')

    def __str__(self):
        return self.name


class Employee(models.Model):
    customId = models.IntegerField()
    empid = models.IntegerField()
    email = models.CharField(max_length=100, primary_key=True)
    password = models.CharField(max_length=50, blank=True)
    mobileNumber = models.IntegerField(default='1234567890')
    date = models.CharField(max_length=100)

    def __str__(self):
        return self.email


class Worker(models.Model):
    customId = models.IntegerField()
    workerId = models.IntegerField()
    email = models.CharField(max_length=100, primary_key=True)
    password = models.CharField(max_length=50, default='123456')
    mobileNumber = models.IntegerField(default='1234567890')
    date = models.CharField(max_length=100)

    def __str__(self):
        return self.email


class Admin(models.Model):
    username = models.CharField(max_length=100, primary_key=True)
    password = models.CharField(max_length=50)
