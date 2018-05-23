# Copyright 2017 The TensorFlow Authors. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ==============================================================================

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function


import argparse
import sys

import numpy as np
import tensorflow as tf
from flask import Flask
from flask import json
from flask import request
from flask import jsonify
app = Flask(__name__)
import scipy.misc
import base64



def load_graph(model_file):
  graph = tf.Graph()
  graph_def = tf.GraphDef()

  with open(model_file, "rb") as f:
    graph_def.ParseFromString(f.read())
  with graph.as_default():
    tf.import_graph_def(graph_def)

  return graph


def read_tensor_from_image_file(file_name,
                                input_height=299,
                                input_width=299,
                                input_mean=0,
                                input_std=255):
  input_name = "file_reader"
  output_name = "normalized"
  file_reader = tf.read_file(file_name, input_name)
  if file_name.endswith(".png"):
    image_reader = tf.image.decode_png(
        file_reader, channels=3, name="png_reader")
  elif file_name.endswith(".gif"):
    image_reader = tf.squeeze(
        tf.image.decode_gif(file_reader, name="gif_reader"))
  elif file_name.endswith(".bmp"):
    image_reader = tf.image.decode_bmp(file_reader, name="bmp_reader")
  else:
    image_reader = tf.image.decode_jpeg(
        file_reader, channels=3, name="jpeg_reader")
  float_caster = tf.cast(image_reader, tf.float32)
  dims_expander = tf.expand_dims(float_caster, 0)
  resized = tf.image.resize_bilinear(dims_expander, [input_height, input_width])
  normalized = tf.divide(tf.subtract(resized, [input_mean]), [input_std])
  sess = tf.Session()
  result = sess.run(normalized)

  return result


def load_labels(label_file):
  label = []
  proto_as_ascii_lines = tf.gfile.GFile(label_file).readlines()
  for l in proto_as_ascii_lines:
    label.append(l.rstrip())
  return label


@app.route('/predict', methods = ['GET','POST'])
def predict():
  content=request.json
  print (request.files)  
  imagefile=request.files['source'] 
  imagefile.save('/home/kaiz/Desktop/new.jpeg')



   # projectpath = request.form['projectFilepath']
  # return projectpath



  # print (content['name'])
  # img_data=content['name']
  # print (img_data)
  # imgdata = base64.b64decode(img_data)
  # filename = 'some_image.jpg'  # I assume you have a way of picking unique filenames
  # with open(filename, 'wb') as f:
  #   f.write(imgdata)


  # print (image_array)
  # fh = open('/home/kaiz/Desktop/outfile.jpeg', "wb")
  # fh.write(image_array.decode('base64'))
  # fh.close()


  # scipy.misc.imsave('/home/kaiz/Desktop/outfile.jpeg', image_array)

  file_name = "tensorflow/examples/label_image/data/grace_hopper.jpg"
  model_file = \
    "tensorflow/examples/label_image/data/inception_v3_2016_08_28_frozen.pb"
  label_file = "tensorflow/examples/label_image/data/imagenet_slim_labels.txt"
  input_height = 299
  input_width = 299
  input_mean = 0
  input_std = 255
  # input_layer = "input"
  # output_layer = "InceptionV3/Predictions/Reshape_1"
  input_layer = "Mul"
  output_layer = "final_result"

  # parser = argparse.ArgumentParser()
  # parser.add_argument("--image", help="image to be processed")
  # parser.add_argument("--graph", help="graph/model to be executed")
  # parser.add_argument("--labels", help="name of file containing labels")
  # parser.add_argument("--input_height", type=int, help="input height")
  # parser.add_argument("--input_width", type=int, help="input width")
  # parser.add_argument("--input_mean", type=int, help="input mean")
  # parser.add_argument("--input_std", type=int, help="input std")
  # parser.add_argument("--input_layer", help="name of input layer")
  # parser.add_argument("--output_layer", help="name of output layer")
  # args = parser.parse_args()

  # if args.graph:
  #   model_file = args.graph
  # if args.image:
  #   file_name = args.image
  # if args.labels:
  #   label_file = args.labels
  # if args.input_height:
  #   input_height = args.input_height
  # if args.input_width:
  #   input_width = args.input_width
  # if args.input_mean:
  #   input_mean = args.input_mean
  # if args.input_std:
  #   input_std = args.input_std
  # if args.input_layer:
  #   input_layer = args.input_layer
  # if args.output_layer:
  #   output_layer = args.output_layer

  #mycode
  model_file ="/tmp/output_graph.pb"
  label_file="/tmp/output_labels.txt"
  input_layer="Mul"
  output_layer="final_result"
  input_mean=128 
  input_std=128 
  # file_name="/home/kaiz/Desktop/pf3.jpeg"

  # file_name=content['imagepath']
  # file_name=projectpath
  file_name='/home/kaiz/Desktop/new.jpeg'

  final=[]



  graph = load_graph(model_file)
  t = read_tensor_from_image_file(
      file_name,
      input_height=input_height,
      input_width=input_width,
      input_mean=input_mean,
      input_std=input_std)

  input_name = "import/" + input_layer
  output_name = "import/" + output_layer
  input_operation = graph.get_operation_by_name(input_name)
  output_operation = graph.get_operation_by_name(output_name)

  with tf.Session(graph=graph) as sess:
    results = sess.run(output_operation.outputs[0], {
        input_operation.outputs[0]: t
    })
  results = np.squeeze(results)

  top_k = results.argsort()[-5:][::-1]
  labels = load_labels(label_file)
  for i in top_k:
    print(labels[i], results[i])
    if results[i]>0.17:
      final.append(labels[i])
      # final.append(labels[i]+"  "+str(results[i]))
  ans="  ".join(final)  
  return ans

 


if __name__ == "__main__":
  app.run(host='127.0.0.1',port=8000,debug=True)
  