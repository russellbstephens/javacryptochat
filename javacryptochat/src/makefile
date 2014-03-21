build:
	javac aes/*.java
	javac server/*.java
	javac driver/*.java

testuser1:
	java driver/DemoChatClient1 -cp .:./aes:./driver:./server

testuser2:
	java driver/DemoChatClient2 -cp .:./aes:./driver:./server

testuser3:
	java driver/DemoChatClient3 -cp .:./aes:./driver:./server

clean:
	rm aes/*.class
	rm server/*.class
	rm driver/*.class
