# Generated by Django 3.0.3 on 2020-03-29 13:43

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('faceApp', '0007_auto_20200326_2245'),
    ]

    operations = [
        migrations.AlterField(
            model_name='image',
            name='description',
            field=models.CharField(default='Some Description', max_length=100),
        ),
    ]