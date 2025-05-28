require('dotenv').config();
const mongoose = require('mongoose');
const Rule = require('./models/Rule');

async function createRules() {
  try {
    await mongoose.connect(process.env.MONGO_URI);

    await Rule.deleteMany({});
    await Rule.insertMany([
        { id: 1, title: 'Cấm hút thuốc', icon: 'smoke_free', status: true },
        { id: 2, title: 'Cấm thú cưng', icon: 'pets', status: true },
        { id: 3, title: 'Cấm tổ chức tiệc', icon: 'celebration', status: true },
        { id: 4, title: 'Cấm mở nhạc lớn sau 10 giờ tối', icon: 'music_off', status: true }
    ]);

    console.log('Rules created');
    await mongoose.disconnect();
  } catch (err) {
    console.error('Error creating rules:', err);
  }
}

createRules();
