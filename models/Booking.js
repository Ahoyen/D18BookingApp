const mongoose = require('mongoose');

const bookingSchema = new mongoose.Schema({
  id: { type: Number, required: true, unique: true },
  roomId: { type: Number, required: true },
  userId: { type: Number, required: true },
  checkIn: { type: String, required: true }, 
  checkOut: { type: String, required: true }, 
  createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('Booking', bookingSchema);