// getAccessToken.js
const {google} = require('googleapis');
const key = require('./aird18bookingapp-firebase-adminsdk-fbsvc-61cc7379d7.json');

const jwtClient = new google.auth.JWT(
  key.client_email,
  null,
  key.private_key,
  ['https://www.googleapis.com/auth/firebase.messaging'],
  null
);

jwtClient.authorize((err, tokens) => {
  if (err) {
    console.error(err);
    return;
  }
  console.log('Access Token:', tokens.access_token);
});
