require('dotenv').config();
const mongoose = require('mongoose');
const Amenity = require('./models/Amenity');

async function createAmenities() {
  try {
    await mongoose.connect(process.env.MONGO_URI);

    await Amenity.deleteMany({});
    await Amenity.insertMany([
        { id: 1, name: 'Wi-Fi', icon: 'wifi' },                  
        { id: 2, name: 'Điều hòa không khí', icon: 'ac_unit' },   
        { id: 3, name: 'Hồ bơi', icon: 'pool' },                  
        { id: 4, name: 'Nhà bếp', icon: 'kitchen' },              
        { id: 5, name: 'Máy giặt', icon: 'local_laundry_service' }
    ]);

    console.log('Amenities created');
    await mongoose.disconnect();
  } catch (err) {
    console.error('Error creating amenities:', err);
  }
}

createAmenities();
