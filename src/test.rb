def puts(*args)
  STDOUT.puts args
  STDOUT.flush
end
puts 'wtf'
puts "hello\ntest"

puts 'ruby process args: '<<ARGV.to_s

ARGV.clear
puts 'ruby process begin'
sleep 2
puts 'ruby process input 1 test'

inp1 = gets.chop
puts 'ruby process input 1: '<<inp1

sleep 2

puts 'ruby process input 2 test'

inp2 = gets.chop
puts 'ruby process input 2: '<<inp2

sleep 2
puts 'ruby process end'