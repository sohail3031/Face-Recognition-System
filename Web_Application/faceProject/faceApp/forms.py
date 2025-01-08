# forms.py
from django import forms
from django.forms import TextInput

from .models import *


class ImageForm(forms.ModelForm):
    class Meta:
        model = Image
        fields = '__all__'
        widgets = {
            "name": forms.TextInput(attrs={'placeholder': 'Name of the Victim', 'name': 'Name', 'id': 'name_id',
                                           'class': 'name_class '}),
            # "description": forms.TextInput(
            #     attrs={'placeholder': 'description', 'name': 'description', 'id': 'description_id',
            #            'class': 'description_class'}),
            # "image": forms.ImageField(attrs={
            #     'id': 'image_id',
            #     'class': 'image_class'
            # }),
            # "loss": forms.IntegerField(attrs={
            #     'id': 'loss_id',
            #     'class': 'loss_class'

            # })
        }


