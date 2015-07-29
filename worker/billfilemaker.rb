path = 'C:\Users\Administrator\git\AT_RF_Client\source\话单文件名称及各记录字段统计.xlsx'

require 'fileutils'
require 'win32ole'
require 'json'

puts path

excel = WIN32OLE