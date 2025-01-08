# Generated by Django 3.0.3 on 2020-05-18 09:59

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('faceApp', '0019_employee_customid'),
    ]

    operations = [
        migrations.CreateModel(
            name='Worker',
            fields=[
                ('customId', models.IntegerField()),
                ('workerId', models.IntegerField()),
                ('email', models.CharField(max_length=100, primary_key=True, serialize=False)),
                ('password', models.CharField(default='123456', max_length=50)),
                ('mobileNumber', models.IntegerField(default='1234567890')),
                ('date', models.CharField(max_length=100)),
            ],
        ),
    ]