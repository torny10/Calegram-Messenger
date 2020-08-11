#!/usr/bin/ruby
require 'fileutils'


unless system('./gradlew --stacktrace assembleAfatRelease')
    puts "BUILD FAILED"
    exit $?
end

apk_dir="TMessagesProj/build/outputs/apk/afat/release"

file_list = []
version = ""
Dir.each_child(apk_dir) {|name|
    m = /\S+afat\S+release-(\S+).apk/.match name
    if m != nil
        version = m[1]
        file_list.push "#{apk_dir}/#{name}"
    end 
}
zip_name = "calegram-#{version}.zip"
FileUtils.rm_rf zip_name
cmd_zip = "zip -j #{zip_name} #{file_list.join ' '}"
puts cmd_zip
system(cmd_zip)