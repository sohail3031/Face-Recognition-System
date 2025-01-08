from keras.models import load_model
from PIL import Image
import cv2
import os
import keras
import numpy as np
from numpy import expand_dims
from mtcnn import MTCNN
import pickle
from django.http import HttpResponse
from django.shortcuts import render, redirect
from .forms import *

detector = MTCNN()
i = []


def loadStaticEmbedding(loaded_model, request):
    if os.path.isfile('C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\model\\file.pkl'):
        print("Loading Previous Data")
    else:
        print("Generating new Data, Please wait")


def loadEmbeddings(loaded_model):
    images = Image.objects.all()
    input_data = load_runtime_data(images)
    recognize_faces_in_cam(input_data, loaded_model)


def load_runtime_data(images):
    from os import path
    if path.exists("C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\model\\file.pkl"):
        return loadStaticData(images)
    else:
        return loadDynamicData(images)


def loadStaticData(images):
    import pickle
    path = "C:/Users/Amair/Documents/faceProject"
    pickle_in = open("C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\model\\file.pkl", "rb")
    previous_data = pickle.load(pickle_in)
    newDict = {k.lower(): v for k, v in previous_data.items()}
    list1 = newDict.keys()
    label_list = []
    print(str(list1))
    for item in list1:
        # print(item)
        # print(item)
        # print(label_list)
        label_list.append(item.split("_")[1])

    faces_embedding = {}
    from keras.models import load_model
    with keras.backend.get_session().graph.as_default():
        loaded_model = load_model(
            'C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\model\\facenet_keras.h5')
        print('Process is Starting Soon')
        for img in images:
            if str(img.customId) in label_list:
                print('Skipping {}'.format(img.name))
                pass
            else:
                # print("customId : {}".format(img.customId))
                label_name = str(img.name.lower()) + "_" + str(img.customId)
                image_path = path + img.image.url
                faces, face_embedding = load_runtime_face(image_path, label_name, loaded_model)
                faces_embedding.update(face_embedding)
                print("{} length {}".format(label_name, len(faces)))
                #             dic[label[0]] = faces
                print('>loaded %d examples for class: %s' % (len(faces), img.name))
        print('Process Completed')
    previous_data.update(faces_embedding)
    f = open("C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\model\\file.pkl", "wb")
    pickle.dump(previous_data, f)
    f.close()
    return previous_data


def loadDynamicData(images):
    path = "C:/Users/Amair/Documents/faceProject"
    previous_data = {}
    faces_embedding = {}
    from keras.models import load_model
    with keras.backend.get_session().graph.as_default():
        loaded_model = load_model(
            'C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\model\\facenet_keras.h5')
        print('Process is Starting Soon')
        for img in images:
            label_name = str(img.name.lower()) + "_" + str(img.customId)
            print("label_name is {}".format(label_name))
            image_path = path + img.image.url
            faces, face_embedding = load_runtime_face(image_path, label_name, loaded_model)
            faces_embedding.update(face_embedding)
            print("{} length {}".format(img.name, len(faces)))
            #             dic[label[0]] = faces
            print('>loaded %d examples for class: %s' % (len(faces), img.name))
        print('Process Completed')
    previous_data.update(faces_embedding)
    print(previous_data)
    f = open("C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\model\\file.pkl", "wb")
    pickle.dump(previous_data, f)
    f.close()
    return previous_data


def load_faces(imageDir, label_name, loaded_model):
    # print("load_face started")
    faces = list()
    faces_embedding = {}
    for filename in os.listdir(imageDir):
        path = imageDir + filename
        if ".ipynb_checkpoints" in path:
            pass
        else:
            face = extract_face(path, detector)
            print(face.shape)
            face_emd = image_to_embedding(face, loaded_model)
            faces.append(face_emd)
    faces_embedding[label_name] = faces
    # print("load_face started")
    return faces, faces_embedding


def load_runtime_face(image_path, label_name, loaded_model):
    faces = list()
    faces_embedding = {}
    if ".ipynb_checkpoints" in image_path:
        pass
    else:
        face = extract_face(image_path, detector)
        print(face.shape)
        face_emd = image_to_embedding(face, loaded_model)
        faces.append(face_emd)
    faces_embedding[label_name] = faces
    return faces, faces_embedding


def load_dataset(directory, loaded_model):
    X, y = list(), list()
    faces_embedding = {}
    for subdir in os.listdir(directory):
        path = directory + subdir + '/'
        if path.startswith('.'):
            pass
        else:
            faces, f = load_faces(path, subdir, loaded_model)
            faces_embedding.update(f)
            label = [subdir for _ in range(len(faces))]
            print("{} length {}".format(label, len(faces)))
            #             dic[label[0]] = faces
            print('>loaded %d examples for class: %s' % (len(faces), subdir))
            X.extend(faces)
            y.extend(label)
        # print("load_dataset ended")
        #     return X, y, faces_embedding
    return faces_embedding


def recognize_faces_in_cam(input_embeddings, loaded_model):
    cv2.namedWindow("Image Recognizer")
    vc = cv2.VideoCapture(0)
    font = cv2.FONT_HERSHEY_SIMPLEX
    face_cascade = cv2.CascadeClassifier(
        'C:\\Users\\Amair\\Documents\\faceProject\\faceApp\\try-8\\haarcascade_frontalface_default.xml')
    while vc.isOpened():
        _, frame = vc.read()
        img = frame
        height, width, channels = frame.shape
        grayImage = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        faces = face_cascade.detectMultiScale(grayImage, 1.3, 5)
        for (x, y, w, h) in faces:
            x1 = x
            y1 = y
            x2 = x + w
            y2 = y + h
            face_image = frame[max(0, y1):min(height, y2), max(0, x1):min(width, x2)]
            identity = recognize_face(face_image, input_embeddings, loaded_model)
            if identity is not None:
                img = cv2.rectangle(frame, (x1, y1), (x2, y2), (255, 255, 255), 2)
                cv2.putText(img, str(identity), (x1 + 5, y1 - 5), font, 1, (255, 255, 255), 2)
        key = cv2.waitKey(100)
        cv2.imshow("Image Recognizer", img)
        if key == 27:
            break
    vc.release()
    cv2.destroyAllWindows()


def recognize_face(face_image, input_embeddings, loaded_model):
    embedding = image_to_embedding(face_image, loaded_model)
    minimum_distance = 200
    name = None
    id = -1
    for (label_name, list_of_images) in input_embeddings.items():
        for image in list_of_images:
            euclidean_distance = np.linalg.norm(embedding - image)
            print('Euclidean distance from %s is %s' % (label_name, euclidean_distance))
            if euclidean_distance < minimum_distance:
                minimum_distance = euclidean_distance
                actualName = label_name.split("_")[0]
                id = label_name.split("_")[1]
                # print("ID: {}".format(id))
                name = actualName
                # print(name)
    if minimum_distance < 10:
        if str(actualName) not in i:
            print("pushing notification for : {}".format(actualName))
            imageObject = Image.objects.get(customId=id)
            imageName = imageObject.name
            description = imageObject.description
            loss = imageObject.loss
            pushNotification(id, imageName, description, loss)
            i.append(actualName)
        return str(name + "     " + str(round(100 - minimum_distance, 3)))
    else:
        return "Unknown"


def pushNotification(id, name, description, loss):
    from pusher_push_notifications import PushNotifications
    beams_client = PushNotifications(
        instance_id='5d6090ee-2b02-4f62-9dfc-0e475f3b54ba',
        secret_key='6F8EA217E680B1915207B2E4B8095E782AC4C538F4777A2B0C858C3A976C9840'
    )
    response = beams_client.publish_to_interests(
        interests=['hello'],
        publish_body={
            'apns': {
                'aps': {
                    'alert': 'Something!'
                }
            },
            'fcm': {
                'notification': {
                    'title': 'Notification Arrived',
                    'body': 'ID: ' + str(id) + ' Name:' + name + ' Description: ' + description + ' Loss:' + str(loss)
                }
            }
        }
    )
    print(response)
    print(response['publishId'])


def image_to_embedding(image, model):
    # print("image_to_embedding started")
    image = cv2.resize(image, (160, 160))
    face_pixels = image.astype('float32')
    mean, std = image.mean(), image.std()
    image = (image - mean) / std
    samples = expand_dims(image, axis=0)
    # K.clear_session()
    face_embedding = model.predict(samples)
    # K.clear_session()
    # print("image_to_embedding started")
    return face_embedding[0]


def extract_face(filename, detector, required_size=(160, 160)):
    # print("extract_face started")
    from PIL import Image
    face_array = list()
    if ".ipynb_checkpoints" in filename:
        print(filename)
        pass
    else:
        image = Image.open(filename)
        image = image.convert('RGB')
        pixels = np.asarray(image)
        detector1 = MTCNN()
        # print("mtcnn")
        results = detector1.detect_faces(pixels)
        # print("mtcnn ended")
        x1, y1, width, height = results[0]['box']
        x1, y1 = abs(x1), abs(y1)
        x2, y2 = x1 + width, y1 + height
        face = pixels[y1:y2, x1:x2]
        image = Image.fromarray(face)
        image = image.resize(required_size)
        face_array = np.asarray(image)
    # print("extract_face ended")
    return face_array
