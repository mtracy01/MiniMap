#!/usr/local/bin/ruby

require 'socket'
require 'timeout'

require './connection_info.rb'

def testGetAllUsers(hostname, port)
	begin
		user1 = nil
		Timeout::timeout(5) do
			user1 = TCPSocket.open(hostname, port)
			user1.puts "id 1"
			user1.puts "getAllUsers"
			response = user1.gets.chomp!
			user1.close
			if (response.eql? "users 1")
				return true
			else
				return false
			end
		end
	rescue Timeout::Error
		if (!user1.nil?)
			user1.close
		end
		return false
	end
end


if __FILE__ == $PROGRAM_NAME
	if (!testGetAllUsers(TEST_HOSTNAME, TEST_PORT))
		puts "\e[31mTest: failed\e[0m"
	end
end

