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

def testCreateFFGameAccept(hostname, port)
	begin
		Timeout::timeout(5) do
			user1 = TCPSocket.open(hostname, port)
			user1.puts "id 1"
			user2 = TCPSocket.open(hostname, port)
			user2.puts "id 2"
	
			user1.puts "createGame friendFinder"
			gameInfo = user1.gets.chomp!

			# Check to see if the game info is correct
			if (!gameInfo.start_with? "game ")
				return false
			end
			
			user2.puts "accept #{gameID.split(' ')[1]}"

			user1.puts "getAllUsers"
			users = user1.gets.chomp!
	
			user1.close
			user2.close
			
			# Test if all users are in the session
			if ((response.eql? "users 1 2") || (response.eql? "users 2 1"))
				return true
			end
		end
	rescue
		return false
	end
end

def testCreateFFGameReject(hostname, port)
	begin
		Timeout::timeout(5) do
			user1 = TCPSocket.open(hostname, port)
			user1.puts "id 1"
			user2 = TCPSocket.open(hostname, port)
			user2.puts "id 2"
	
			user1.puts "createGame friendFinder"
			gameInfo = user1.gets.chomp!

			# Check to see if the game info is correct
			if (!gameInfo.start_with? "game ")
				return false
			end
			
			user2.puts "reject #{gameID.split(' ')[1]}"

			user1.puts "getAllUsers"
			users = user1.gets.chomp!
	
			user1.close
			user2.close
			
			# Test if all users are in the session
			if (response.eql? "users 1")
				return true
			end
		end
	rescue
		return false
	end
end

hostname = 'localhost'
port = 2048

errors = 0
tests = 4

puts "These are critical server tests.  All of these must pass."

if (!testConnection(hostname, port))
	puts "\e[31mTest: connection failed\e[0m"
	errors = errors + 1
end
sleep 1
if (!testGetAllUsers(hostname, port))
	puts "\e[31mTest: get all users failed\e[0m"
	errors = errors + 1
end
sleep 1
if (!testCreateFFGameAccept(hostname, port))
	puts "\e[31mTest: friend finder create with accept failed\e[0m"
	errors = errors + 1
end
sleep 1
if (!testCreateFFGameAccept(hostname, port))
	puts "\e[31mTest: friend finder create with reject failed\e[0m"
	errors = errors + 1
end

puts "\e[#{(errors == 0) ? 32 : 31}m#{errors}\e[0m test(s) failed."
puts "#{tests} total tests."
if (errors == 0)
	puts "All tests passed."
end
