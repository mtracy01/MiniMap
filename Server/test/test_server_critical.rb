#!/usr/local/bin/ruby

require 'socket'
require 'timeout'

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
		if (!user1.nil?)
			user1.close
		end
		false
	end
end

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

def testCreateFFGameAcceptWithInvites(hostname, port)
	begin
		user1 = nil
		user2 = nil
		Timeout::timeout(5) do
			user1 = TCPSocket.open(hostname, port)
			user1.puts "id 1"
			user2 = TCPSocket.open(hostname, port)
			user2.puts "id 2"
	
			user1.puts "createGame friendFinder"
			gameInfo = user1.gets.chomp!
			# Check to see if the game info is correct
			if (!gameInfo.start_with? "game ")
				user1.close
				user2.close
				return false
			end
			user1.puts "invite #{gameInfo.split(' ')[1]} 2"
			invite = user2.gets.chomp!
			if (!invite.start_with? "invite ")
				user1.close
				user2.close
				return false
			end
			user2.puts "accept #{invite.split(' ')[2]}"
			users1 = user1.gets.chomp!
			users2 = user2.gets.chomp!
			
			userParts = users1.split(' ')
			user1.puts "start #{gameInfo.split(' ')[1]}"
			startMessage1 = user1.gets.chomp!
			startMessage2 = user2.gets.chomp!
			user1.close
			user2.close
			
			# Test if all users are in the session
			if (!(userParts[0].eql? "gameUsers") || !(userParts.length == 4))
				return false
			end
			if (!(startMessage1.eql? "gameStart #{gameInfo.split(' ')[1]}") || !(startMessage2.eql? "gameStart #{gameInfo.split(' ')[1]}"))
				return false
			end
			return true
		end
	rescue Timeout::Error
		puts "timeout"
		if (!user1.nil?)
			user1.close
		end
		if (!user2.nil?)
			user2.close
		end

		return false
	end
end

def testCreateFFGameAccept(hostname, port)
	begin
		user1 = nil
		user2 = nil
		Timeout::timeout(5) do
			user1 = TCPSocket.open(hostname, port)
			user1.puts "id 1"
			user2 = TCPSocket.open(hostname, port)
			user2.puts "id 2"
	
			user1.puts "createGame friendFinder"
			gameInfo = user1.gets.chomp!

			# Check to see if the game info is correct
			if (!gameInfo.start_with? "game ")
				user1.close
				user2.close
				return false
			end
			
			user2.puts "accept #{gameInfo.split(' ')[1]}"
			users1 = user1.gets.chomp!
			users2 = user2.gets.chomp!
			
			userParts = users1.split(' ')

			user1.puts "start #{gameInfo.split(' ')[1]}"
			startMessage1 = user1.gets.chomp!
			startMessage2 = user2.gets.chomp!

			user1.close
			user2.close
			
			# Test if all users are in the session
			if (!(userParts[0].eql? "gameUsers") || !(userParts.length == 4))
				return false
			end
			if (!(startMessage1.eql? "gameStart #{gameInfo.split(' ')[1]}") || !(startMessage2.eql? "gameStart #{gameInfo.split(' ')[1]}"))
				return false
			end
			return true
		end
	rescue Timeout::Error
		puts "timeout"
		if (!user1.nil?)
			user1.close
		end
		if (!user2.nil?)
			user2.close
		end

		return false
	end
end

def testCreateFFGameReject(hostname, port)
	begin
		user1 = nil
		user2 = nil
		Timeout::timeout(5) do
			user1 = TCPSocket.open(hostname, port)
			user1.puts "id 1"
			user2 = TCPSocket.open(hostname, port)
			user2.puts "id 2"
	
			user1.puts "createGame friendFinder"
			gameInfo = user1.gets.chomp!
			
			# Check to see if the game info is correct
			if (!gameInfo.start_with? "game ")
				user1.close
				user2.close
				return false
			end
			
			user2.puts "reject #{gameInfo.split(' ')[1]}"

			users = user1.gets.chomp!
			userParts = users.split(' ')
	
			user1.close
			user2.close
			
			# Test if all users are in the session
			if ((userParts[0].eql? "gameUsers") && (userParts.length == 3) && (userParts[2] == "1"))
				return true
			end
		end
	rescue Timeout::Error
		if (!user1.nil?)
			user1.close
		end
		if (!user2.nil?)
			user2.close
		end
		return false
	end
end

hostname = 'localhost'
port = 2048

errors = 0
tests = 3

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
#sleep 1
#if (!testCreateFFGameAccept(hostname, port))
#	puts "\e[31mTest: friend finder create with accept failed\e[0m"
#	errors = errors + 1
#end
#sleep 1
#if (!testCreateFFGameReject(hostname, port))
#	puts "\e[31mTest: friend finder create with reject failed\e[0m"
#	errors = errors + 1
#end
sleep 1
if (!testCreateFFGameAcceptWithInvites(hostname, port))
	puts "\e[31mTest: friend finder create with accept (with invites) failed\e[0m"
	errors = errors + 1
end

puts "\e[#{(errors == 0) ? 32 : 31}m#{errors}\e[0m test(s) failed."
puts "#{tests} total tests."
if (errors == 0)
	puts "All tests passed."
end
