#!/usr/local/bin/ruby

require 'socket'
require 'timeout'

require './connection_info.rb'

# Add two users
# Send flag info, send los info
# start the game
# both users should get flag, los, team info

# test tagging

# test capturing a flag


def testCTFStartGame(hostname, port)
	begin
		user1 = nil
		user2 = nil
		Timeout::timeout(5) do
			user1 = TCPSocket.open(hostname, port)
			user1.puts "id 1"
			user2 = TCPSocket.open(hostname, port)
			user2.puts "id 2"
			
			debug("Creating game")

			user1.puts "createGame ctf"
			gameInfo = user1.gets.chomp!
			# Check to see if the game info is correct
			if (!gameInfo.start_with? "game ")
				user1.close
				user2.close
				return false
			end

			gameNum = gameInfo.split(' ')[1]
			debug("Game num: #{gameNum}")

			user1.puts "invite #{gameNum} 2"

			invite2 = user2.gets.chomp!
			if (!invite2.start_with? "invite ")
				debug("Invite 2 not correnct")
				user1.close
				user2.close
				return false
			end
			debug ("Invites correct")
			
			user2.puts "accept #{gameNum}"
			# ignore the game user messages
			user1.gets
			gameUsers = user2.gets

			userParts = gameUsers.split(' ')
			# Test if all users are in the session
			if (!(userParts[0].eql? "gameUsers") || !(userParts.length == 4))
				debug("Game users incorrect: #{gameUsers}")
				user1.close
				user2.close
				return false
			end

			debug("Accepts done")

			user1.puts "start #{gameNum}"
			user1.gets
			user2.gets
			startMessage = user3.gets.chomp!
			
			if (!startMessage.eql? "gameStart #{gameNum}")
				debug("Game start not received")
				user1.close
				user2.close
				return false
			end
			
			debug("Game started")


			target1 = (user1.gets.chomp!).split(' ')[1]
			target2 = (user2.gets.chomp!).split(' ')[1]
			target3 = (user3.gets.chomp!).split(' ')[1]

			debug("Targets: 1 -> #{target1}, 2 -> #{target2}, 3 -> #{target3}")
			if (!(target1.eql? "2") || !(target2.eql? "3") || !(target3.eql? "1"))
				debug("Targets assigned incorrectly")
				user1.close
				user2.close
				return false
			end

			debug("Targets assigned correctly")

			user1.puts "location 40.426 -86.924"
			user1.gets
			locationMessage = user3.gets.chomp!

			if (!(locationMessage.start_with? "location 1 "))
				debug("location received incorrectly: #{locationMessage}")
				user1.close
				user2.close
				return false
			end
			debug("Location received")

			user3.puts "location 40.426 -86.924"
			user2.gets
			user3.gets

			acceptDeath = user1.gets.chomp!
			acceptKill = user3.gets.chomp!

			if (!(acceptDeath.eql? "acceptDeath 3") || !(acceptKill.eql? "acceptKill 1"))
				debug("Accept death/kill messages incorrect")
				user1.close
				user2.close
				return false
			end

			debug("Accept death/kill messages correct")
			user1.puts("confirmDeath true")
			user3.puts("confirmKill true")

			killMes = user1.gets.chomp!
			user2.gets
			user3.gets

			debug("#{killMes}")
			if (!(killMes.eql? "kill 3 1"))
				debug("Kill not reported")
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

def debug(line)
	if __FILE__ == $PROGRAM_NAME
		puts line
	end
end

if __FILE__ == $PROGRAM_NAME
	if (!testCTFStartGame(TEST_HOSTNAME, TEST_PORT))
		puts "\e[31mTest: failed\e[0m"
	end
end




