#!/bin/bash

file_names=''

for file in `find . -name '*Spec.scala' -type f -mtime -14`
do
  name_without_path=$(basename $file)
  name_without_ext="${name_without_path%.*}"

  file_names=$file_names" "$name_without_ext
done

spec_command="sbt 'test-only $(echo $file_names)'"

eval $spec_command
