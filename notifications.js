/* Creating functions to send notifications to devices
    Code premise borrowed from firebase functions-samples
    Github, located at
    https://github.com/firebase/functions-samples/blob/master/fcm-notifications/functions/index.js
*/


'use strict';


const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

/**
 * Triggers when a user gets a new follower and sends a notification.
 *
 * Followers add a flag to `/users/{userUid}/downs`.
 */
exports.sendDownNotification = functions.database
    .ref('/users/{userUid}/downs/{downUid}')
    .onCreate((snapshot, context) => {
      // getting the two parameters, where the data is changed
      const userUid = context.params.userUid;
      const downUid = context.params.downUid;
      console.log('We have a new down UID:', downUid, 'for user:', userUid);

      // Get a database reference to our posts
      var db = admin.database();

      /*
      downRef.on("value", snapshot => {
        console.log("All snapshot data: " + snapshot.val());
        console.log("Getting down info: ");
        downCreatorUid = snapshot.child(downUid).child("creator").val();
        downName = snapshot.child(downUid).child("title").val();
        console.log("Down Creator Uid: " + downCreatorUid);
        console.log("Down Name: " + downName);
      });
      */


      // reference to head
      //var ref = db.ref("users/" + userUid + "/token");
      var ref = db.ref();

      // Attach an asynchronous callback to read the data at our posts reference
      ref.off(); // eliminate duplicates
      ref.on("value", snapshot => {
        ref.off();
        // get token to send
        var token = snapshot.child("users").child(userUid).child("token").val();
        // get creator Uid
        var downCreatorUid = snapshot.child("down").child(downUid).child("creator").val();
        // sender and reciever are the same
        if (downCreatorUid === userUid) {
          console.log("Don't send to creator: " + downCreatorUid + " " + userUid);
          return;
        }
        // get creator name
        var downCreatorName = snapshot.child("users").child(downCreatorUid).child("name").val();
        // get down name
        var downName = snapshot.child("down").child(downUid).child("title").val();

        console.log("Down Creator Uid: " + downCreatorUid);
        console.log("Down Name: " + downName);
        console.log("Token: " + token); //Printing the token

        const payload = { // defining message to send to device
          notification: {
            title: downCreatorName + ' invited you to a down!',
            body: downName
          },
            data : {
              type: "down"
          }
        };

        // snapshot.val() is the token
        // sending the message
        admin.messaging().sendToDevice(token, payload);
      }, 
      
      
      errorObject => {
        console.log("The read failed: " + errorObject.code);
      });
      return;
    });
  

    /**
 * Triggers when a user gets a new follower and sends a notification.
 *
 * Followers add a flag to `/users/{userUid}/requests/{friendUid}`.
 */
exports.sendFriendRequestNotification = functions.database
.ref('/users/{userUid}/requests/{friendUid}')
.onCreate((snapshot, context) => {
  // getting the two parameters, where the data is changed
  const userUid = context.params.userUid;
  const friendUid = context.params.friendUid;
  console.log('Friend Request: ', friendUid, 'for user:', userUid);

  // Get a database reference to our posts
  var db = admin.database();

  // reference to head
  //var ref = db.ref("users/" + userUid + "/token");
  var ref = db.ref("users");

  // Attach an asynchronous callback to read the data at our posts reference
  ref.off(); // eliminate duplicates
  ref.on("value", snapshot => {
    ref.off();
    // get token to send
    var token = snapshot.child(userUid).child("token").val();
    // get creator Uid
    var friendName = snapshot.child(friendUid).child("name").val();
    // sender and reciever are the same
    console.log("Friend Name: " + friendName);
    console.log("Token: " + token); //Printing the token

    const payload = { // defining message to send to device
      notification: {
        title: 'New friend request!',
        body: friendName,
      },
      data : {
        type: "request"
      }
    };

    // snapshot.val() is the token
    // sending the message
    admin.messaging().sendToDevice(token, payload);
  },   
  errorObject => {
    console.log("The read failed: " + errorObject.code);
  });
  return;
});


/**
 * Triggers when a user gets a status update and sends a notification.
 *
 * Followers add a flag to `/users/{userUid}/downs`.
 */
exports.sendUpdateStatusNotification = functions.database
    .ref('/down/{downUid}/status/{statusUserUid}')
    .onUpdate((snapshot, context) => {
      // getting the two parameters, where the data is changed
      const downUid = context.params.downUid;
      const statusUserUid = context.params.statusUserUid;
      console.log('We have a new down status UID:', downUid, 'for user:', statusUserUid);
      console.log('User the created the status: ', statusUserUid);

      // Get a database reference to our posts
      var db = admin.database();


      // reference to head
      //var ref = db.ref("users/" + userUid + "/token");
      var ref = db.ref();

      // Attach an asynchronous callback to read the data at our posts reference
      ref.off(); // eliminate duplicates
      ref.on("value", snapshot => {
        ref.off();
        // name of status creator
        var statusUserName = snapshot.child("users").child(statusUserUid).child("name").val();
        // message content
        var statusContent = snapshot.child("down").child(downUid).child("status").child(statusUserUid).val();
        // down name
        var downName = snapshot.child("down").child(downUid).child("title").val();

        console.log(statusUserName + " created a new status: " + statusContent + " in down: " + downName);

        var invitees = snapshot.child("down").child(downUid).child("status").val();

        var inviteesUids = Object.keys(invitees);

        for (var i = 0; i < inviteesUids.length; i++){
          if (statusUserUid !== inviteesUids[i]) {
            const payload = { // defining message to send to device
              notification: {
                title: statusUserName + ' added a new status in ' + downName,
                body: statusContent
              },
                data : {
                  type: "status"
              }
            };
          // get token to send
          var token = snapshot.child("users").child(inviteesUids[i]).child("token").val();
          console.log("Sucessfully sent message to: " + token);
          // snapshot.val() is the token
          // sending the message
          admin.messaging().sendToDevice(token, payload);
        }
      }
      return;
      }, 
      
      errorObject => {
        console.log("The read failed: " + errorObject.code);
      });
      return;
    });


    /**
 * Triggers when a user gets a status update and sends a notification.
 *
 * Followers add a flag to `/users/{userUid}/downs`.
 */
exports.sendDownDeleteNotification = functions.database
.ref('/down/{downUid}')
.onDelete((snapshot, context) => {
  // getting the two parameters, where the data is changed

  // getting down Uid
  const downUid = context.params.downUid;

  console.log('Down has been deleted:', downUid);

  // getting downName
  const downName = snapshot.child("title").val();
  // down creator name
  const creatorUid = snapshot.child("creator").val();

  // Map of invitees
  const invitees = snapshot.child("invited").val();
  // String[] of Uids
  const inviteesUids = Object.keys(invitees);


  // Attach an asynchronous callback to read the data at our posts reference
  var db = admin.database(); // grab db ref
  var ref = db.ref();
  ref.off(); // eliminate duplicates
  ref.on("value", snapshot => {
    ref.off();

    // name of status creator
    var creatorUserName = snapshot.child("users").child(creatorUid).child("name").val();
    //console.log(statusUserName + " created a new status: " + statusContent + " in down: " + downName);
    for (var i = 0; i < inviteesUids.length; i++){
      if (creatorUid !== inviteesUids[i]) {
        const payload = { // defining message to send to device
          notification: {
            title: "Down deleted",
            body: creatorUserName + ' deleted ' + downName,
          },
            data : {
              type: "down_deleted"
          }
        };
      // get token to send
      var token = snapshot.child("users").child(inviteesUids[i]).child("token").val();
      console.log("Sucessfully sent message to: " + token);
      // snapshot.val() is the token
      // sending the message
      admin.messaging().sendToDevice(token, payload);
    }
  }
  return;
  }, 
  
  errorObject => {
    console.log("The read failed: " + errorObject.code);
  });
  return;

});