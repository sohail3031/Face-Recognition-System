# Generated by Django 3.0.3 on 2020-03-30 07:01

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('faceApp', '0011_auto_20200330_1224'),
    ]

    operations = [
        migrations.AlterField(
            model_name='image',
            name='description',
            field=models.TextField(blank=True, max_length=100),
        ),
    ]