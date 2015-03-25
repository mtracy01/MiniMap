#!/usr/local/bin/ruby

require 'socket'
require 'timeout'

require './connection_info.rb'

def testRemoveUserFF(hostname, port)
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
			
			# Test if all users are in the session
			if (!(userParts[0].eql? "gameUsers") || !(userParts.length == 4))
				user1.close
				user2.close
				return false
			end
			if (!(startMessage1.eql? "gameStart #{gameInfo.split(' ')[1]}") || !(startMessage2.eql? "gameStart #{gameInfo.split(' ')[1]}"))
				user1.close
				user2.close
				return false
			end

			if __FILE__ == $PROGRAM_NAME
				puts "Game started correctly, removing user 2"
			end

			# Now that everyone is in the game, let us remove user 2
			user1.puts "remove #{gameInfo.split(' ')[1]} 2"
			if __FILE__ == $PROGRAM_NAME
				puts "remove message sent"
			end
			removeUser1 = user1.gets.chomp!
			if __FILE__ == $PROGRAM_NAME
				puts "final users 1 received: #{removeUser1}"
			end
			removeUser2 = user2.gets.chomp!
			if __FILE__ == $PROGRAM_NAME
				puts "final users 2 received: #{removeUser2}"
			end

			if (!(removeUser1.eql? "userRemoved 2"))
				if __FILE__ == $PROGRAM_NAME
					puts "remove message 1 incorrect"
				end
				user1.close
				user2.close
				return false
			end
			if (!(removeUser2.eql? "userRemoved 2"))
				if __FILE__ == $PROGRAM_NAME
					puts "remove message 2 incorrect"
				end
				user1.close
				user2.close
				return false
			end

			user1.close
			user2.close
			return true
		end
	rescue Timeout::Error
		if __FILE__ == $PROGRAM_NAME
			puts "timeout"
		end
		if (!user1.nil?)
			user1.close
		end
		if (!user2.nil?)
			user2.close
		end

		return false
	end
end



if __FILE__ == $PROGRAM_NAME
	if (!testRemoveUserFF(TEST_HOSTNAME, TEST_PORT))
		puts "\e[31mTest: failed\e[0m"
	end
end


