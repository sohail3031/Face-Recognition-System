# Generated by Django 3.0.3 on 2020-03-14 15:52

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Image',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(default='Name', max_length=50)),
                ('image', models.ImageField(upload_to='images/')),
                ('description', models.TextField(default='Necessary Description', max_length=500)),
                ('loss', models.IntegerField(default=1000)),
            ],
        ),
    ]
