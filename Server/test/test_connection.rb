#!/usr/local/bin/ruby

require 'socket'
require 'timeout'

require './connection_info.rb'

def testConnection(hostname, port)
	begin
		user1 = nil
		Timeout::timeout(5) do
			begin
				user1 = TCPSocket.open(hostname, port)
				user1.puts "id 1"
				user1.close
				true
			rescue Errno::ECONNREFUSED, Errno::EHOSTUNREACH
				false
			end
		end
	rescue Timeout::Error
		if __FILE__ == $PROGRAM_NAME
			puts "timeout"
		end
		if (!user1.nil?)
			user1.close
		end
		false
	end
end


if __FILE__ == $PROGRAM_NAME
	if (!testConnection(TEST_HOSTNAME, TEST_PORT))
		puts "\e[31mTest: failed\e[0m"
	end
end
