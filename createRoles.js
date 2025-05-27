require('dotenv').config();    // Load biến môi trường từ .env
const mongoose = require('mongoose');
const Role = require('./models/Role');

async function createRoles() {
  try {
    // Dùng biến môi trường MONGO_URI
    await mongoose.connect(process.env.MONGO_URI);

    await Role.deleteMany({});
    await Role.insertMany([
      { id: 1, name: 'user' },
      { id: 2, name: 'admin' },
      { id: 3, name: 'host' }
    ]);

    console.log('Roles created');
    await mongoose.disconnect();
  } catch (err) {
    console.error('Error creating roles:', err);
  }
}
createRoles();
