# Generated by Django 3.0.3 on 2020-05-16 16:10

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('faceApp', '0018_auto_20200514_2330'),
    ]

    operations = [
        migrations.AddField(
            model_name='employee',
            name='customId',
            field=models.IntegerField(default=100),
            preserve_default=False,
        ),
    ]
