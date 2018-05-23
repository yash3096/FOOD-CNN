from keras.applications import ResNet50
from keras.applications import InceptionV3
from keras.applications import Xception # TensorFlow ONLY
from keras.applications import VGG16
from keras.applications import VGG19
from keras.applications import imagenet_utils
from keras.applications.inception_v3 import preprocess_input
from keras.preprocessing.image import img_to_array
from keras.preprocessing.image import load_img
import numpy as np
import argparse
import cv2
from keras.applications.vgg16 import VGG16
from keras.preprocessing import image
from keras.applications.vgg16 import preprocess_input
from keras.layers import Input, Flatten, Dense
from keras.models import Model
import numpy as np
from keras.applications.inception_v3 import InceptionV3
from keras.preprocessing import image
from keras.models import Model
from keras.layers import Dense, GlobalAveragePooling2D
from keras import backend as K
from keras import applications
from keras.preprocessing.image import ImageDataGenerator
from keras import optimizers
from keras.models import Sequential
from keras.layers import Dropout, Flatten, Dense
from keras.models import model_from_json
import os
from PIL import Image

# construct the argument parse and parse the arguments
# ap = argparse.ArgumentParser()
# ap.add_argument("-i", "--image", required=True,
# 	help="path to the input image")
# ap.add_argument("-model", "--model", type=str, default="vgg16",
# 	help="name of pre-trained network to use")
# args = vars(ap.parse_args())


args={}

args["model"]="inception"
args["image"]="images12.jpg"
#filelist = 'images2.jpg', 'images3.jpg', 'images4.jpg', 'images5.jpg', 'images6.jpg', 'images7.jpg', 'images8.jpg', 'images9.jpg', 'images10.jpg', 'images11.jpg', 'images12.jpg', 'images13.jpg', 'images14.jpg', 'images15.jpg'

# define a dictionary that maps model names to their classes
# inside Keras
MODELS = {
	"vgg16": VGG16,
	"vgg19": VGG19,
	"inception": InceptionV3,
	"xception": Xception, # TensorFlow ONLY
	"resnet": ResNet50
}
 
# esnure a valid model name was supplied via command line argument
if args["model"] not in MODELS.keys():
	raise AssertionError("The --model command line argument should "
		"be a key in the `MODELS` dictionary")



# initialize the input image shape (224x224 pixels) along with
# the pre-processing function (this might need to be changed
# based on which model we use to classify our image)
inputShape = (224, 224)
preprocess = imagenet_utils.preprocess_input
 
# if we are using the InceptionV3 or Xception networks, then we
# need to set the input shape to (299x299) [rather than (224x224)]
# and use a different image processing function
if args["model"] in ("inception", "xception"):
	inputShape = (299, 299)
	preprocess = preprocess_input


# load our the network weights from disk (NOTE: if this is the
# first time you are running this script for a given network, the
# weights will need to be downloaded first -- depending on which
# network you are using, the weights can be 90-575MB, so be
# patient; the weights will be cached and subsequent runs of this
# script will be *much* faster)
print("[INFO] loading {}...".format(args["model"]))
Network = MODELS[args["model"]]
model = Network(weights="imagenet")


# load the input image using the Keras helper utility while ensuring
# the image is resized to `inputShape`, the required input dimensions
# for the ImageNet pre-trained network
print("[INFO] loading and pre-processing image...")
images = load_img(args["image"], target_size=inputShape)
images = img_to_array(images)
print(images)
images = np.expand_dims(images, axis=0)
images = preprocess(images)
print(images)
'''for image in filelist:
    temp1=load_img(image, target_size=inputShape)
    temp=img_to_array(temp1)
    final= np.concatenate((images, temp))

print(final)'''
'''for fname in filelist
    image = Image.open(fname)
    image = load_img(args["image"], target_size=inputShape)
    image = img_to_array(image)

print (x)
'''

# our input image is now represented as a NumPy array of shape
# (inputShape[0], inputShape[1], 3) however we need to expand the
# dimension by making the shape (1, inputShape[0], inputShape[1], 3)
# so we can pass it through thenetwork
'''for i in range(1,15):
    images[i].astype(np.float64)
    print(images[i])
    images[i] = np.expand_dims(images[i], axis=0)
# pre-process the image using the appropriate function based on the
# model that has been loaded (i.e., mean subtraction, scaling, etc.)
    images[i] = preprocess(images[i])
'''
#print(images)
# classify the image
print("[INFO] classifying image with '{}'...".format(args["model"]))







# # Generate a model with all layers (with top)
# vgg16 = VGG16(weights=None, include_top=True)

# #Add a layer where input is the output of the  second last layer 
# x = Dense(1000, activation='softmax', name='predictions')(vgg16.layers[-2].output)

# #Then create the corresponding model 
# my_model = Model(input=vgg16.input, output=x)
# my_model.summary()






json_file = open('model1.json', 'r')
loaded_model_json = json_file.read()
json_file.close()
model = model_from_json(loaded_model_json)
# load weights into new model
model.load_weights("model1.h5")
print("Loaded model from disk")

preds = model.predict(images)

print(preds)


'''

OUTPUTS

 SAMOSA-
[[0.04651216 0.00362068 0.9498671 ]]


[[3.886774e-20 0.000000e+00 1.000000e+00]]


[[2.4788927e-01 2.2844302e-38 7.5211072e-01]]



PIZZA-
[[1.5311289e-17 1.0000000e+00 3.3046989e-33]]


[[2.4095212e-09 1.0000000e+00 1.2565894e-15]]


[[3.9311772e-29 1.0000000e+00 0.0000000e+00]]



OMLETTE-
[[2.2463976e-08 4.2514346e-18 1.0000000e+00]]


[[2.099671e-05 9.999790e-01 9.192790e-16]]


[[3.6804086e-05 9.9979132e-01 1.7191944e-04]] - SAME IMAGE


[[3.6804086e-05 9.9979132e-01 1.7191944e-04]] - SAME IMAGE
 

[[3.6804086e-05 9.9979132e-01 1.7191944e-04]] - SAME IMAGE


for doughnut - [[1.1261595e-10 6.5445480e-21 1.0000000e+00]]
for doughnut - [[1.1119520e-06 8.5059735e-23 9.9999893e-01]]
for doughnut - [[1.9555158e-23 1.0000000e+00 0.0000000e+00]]

for pizza  -  [[7.8322074e-08 9.9999988e-01 4.5771763e-14]]
for pizza  -  [[9.6410477e-01 3.5893235e-02 1.9708571e-06]]
[[1.0000000e+00 2.4548136e-14 2.6449481e-17]]
[[9.9977916e-01 1.9440431e-20 2.2086229e-04]]
[[5.0712033e-13 1.0000000e+00 6.4567873e-29]]
[[4.7830967e-03 9.9521697e-01 4.5963125e-29]]
[[1.5641246e-12 1.0000000e+00 0.0000000e+00]]



for samosa - 
[[9.9999964e-01 2.2739280e-25 4.0301765e-07]]
[[9.9999344e-01 6.5629242e-06 8.8559952e-33]]
[[3.0449542e-06 9.9999690e-01 2.8330937e-11]]
[[4.7797296e-02 9.1057691e-29 9.5220274e-01]]


  OUTPUT  2 

DONUT-
[[0. 1. 0.]]
[[9.9999714e-01 6.6195372e-07 2.1316978e-06]]
[[1.9303215e-18 1.0000000e+00 5.8291742e-36]]
[[9.9991000e-01 6.9383779e-05 2.0572374e-05]]
[[1.00000e+00 8.41206e-28 0.00000e+00]]
[[0. 1. 0.]]
[[0. 1. 0.]]
[[1.0000000e+00 1.3351364e-12 1.6188026e-09]]


PIZZA-
[[1.4356713e-24 1.0000000e+00 0.0000000e+00]]
[[1.6796803e-24 1.0000000e+00 0.0000000e+00]]
[[4.6376683e-12 1.0000000e+00 1.8586051e-32]]


[[0. 1. 0.]]
[[8.321363e-11 1.000000e+00 0.000000e+00]]
[[1.0000000e+00 0.0000000e+00 2.4633065e-15]]




SAMOSA-
[[9.9897987e-01 1.0200613e-03 1.4014053e-13]]
[[1.0000000e+00 0.0000000e+00 1.1587389e-13]]
[[1.000000e+00 0.000000e+00 3.363283e-22]]



{'donut': 0, 'samosa': 2, 'pizza': 1}

'''
