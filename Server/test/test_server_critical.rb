#!/usr/local/bin/ruby

require 'socket'
require 'timeout'

require './connection_info.rb'
require './test_connection.rb'
require './test_get_all_users.rb'
require './test_create_ff_game_accept_with_invites.rb'
require './test_remove_user_ff.rb'
require './test_assassins_game.rb'


errors = 0
tests = 5

puts "These are critical server tests.  All of these must pass."

if (!testConnection(TEST_HOSTNAME, TEST_PORT))
	puts "\e[31mTest: connection failed\e[0m"
	errors = errors + 1
end
sleep 1
if (!testGetAllUsers(TEST_HOSTNAME, TEST_PORT))
	puts "\e[31mTest: get all users failed\e[0m"
	errors = errors + 1
end
sleep 1
if (!testCreateFFGameAcceptWithInvites(TEST_HOSTNAME, TEST_PORT))
	puts "\e[31mTest: friend finder create with accept (with invites) failed\e[0m"
	errors = errors + 1
end
sleep 1
if (!testRemoveUserFF(TEST_HOSTNAME, TEST_PORT))
	puts "\e[31mTest: remove user friend finder failed\e[0m"
	errors = errors + 1
end
sleep 1
if (!testAssassinsGame(TEST_HOSTNAME, TEST_PORT))
	puts "\e[31mTest: assassins game failed\e[0m"
	errors = errors + 1
end


puts "\e[#{(errors == 0) ? 32 : 31}m#{errors}\e[0m test(s) failed."
puts "#{tests} total tests."
if (errors == 0)
	puts "All tests passed."
end
