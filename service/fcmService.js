// const axios = require('axios');
// const { GoogleAuth } = require('google-auth-library');
// const PROJECT_ID = 'aird18bookingapp';

// async function getAccessToken() {
//   const auth = new GoogleAuth({
//     keyFile:  '../aird18bookingapp-firebase-adminsdk-fbsvc-61cc7379d7.json', // file JSON bạn tải từ Firebase Console
//     scopes: ['https://www.googleapis.com/auth/firebase.messaging'],
//   });

//   const client = await auth.getClient();
//   const tokenResponse = await client.getAccessToken();
//   return tokenResponse.token;
// }

// async function sendFCMNotification(fcmToken, messageContent) {
//   const accessToken = await getAccessToken();

//   const payload = {
//     message: {
//       token: fcmToken,
//       notification: {
//         title: 'Thông báo từ AirD18',
//         body: messageContent,
//       },
//       data: {
//         content: messageContent
//       }
//     }
//   };

//   try {
//     const res = await axios.post(
//       `https://fcm.googleapis.com/v1/projects/${PROJECT_ID}/messages:send`,
//       payload,
//       {
//         headers: {
//           Authorization: `Bearer ${accessToken}`,
//           'Content-Type': 'application/json',
//         },
//       }
//     );

//     console.log('✅ Notification sent:', res.data);
//   } catch (error) {
//     console.error('❌ Error sending FCM:', error.response?.data || error.message);
//   }
// }
