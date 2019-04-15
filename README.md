Conner Delahanty, cdelaha1
Trenton Wang, ywang382
Vance Wood, vwood3
William Cho, wcho9

Team I - Down

This is the repository for UIMA 601.290 in Spring 2019. 

The purpose of this repository is to create and implement the Down App as
defined in the first part of the semester.

The current state of the application has a feed with the ability to 
create down, my friends with the ability to add friends and manage 
requests, and my account with the ability to see the status of your
account and sign out.

Present observable issues include:
1. due to asyncronous property of firebase, spamming the down button may
	cause inaccurate attendee values for downs, however this is a
	property of firebase real time database and cannot yet be fixed
2. there are minor text wrapping uses for longer strings, such as a title
	for a down that is very long. input vetting will be fixed in 
	the next sprint

----Testing login information for grader----
username: requests@password1234.com
passowrd: 1234


