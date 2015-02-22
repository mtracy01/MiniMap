#!/usr/local/bin/ruby

require 'socket'
require 'timeout'

def testConnection(hostname, port)
	begin
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
	rescue
		false
	end
end

def testGetAllUsers(hostname, port)
	begin
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
	rescue
		return false
	end
end

def testCreateFFGame(hostname, port)
	begin
		Timeout::timeout(5) do
			user1 = TCPSocket.open(hostname, port)
			user1.puts "id 1"
			user2 = TCPSocket.open(hostname, port)
			user2.puts "id 2"
	
			user1.puts "createGame friendFinder 2"
			invite = user2.gets.chomp!
	
			user1.close
			user2.close
	
			if (invite.start_with? "invite friendFinder")
				return true
			else
				return false
			end
		end
	rescue
		return false
	end
end

hostname = 'localhost'
port = 2048

errors = 0

if (!testConnection(hostname, port))
	puts "Test: connection failed"
	errors = errors + 1
	exit
end
sleep 1
if (!testGetAllUsers(hostname, port))
	puts "Test: get all users failed"
	errors = errors + 1
end
sleep 1
if (!testCreateFFGame(hostname, port))
	puts "Test: friend finder create failed"
	errors = errors + 1
end

puts "#{errors} test(s) failed."