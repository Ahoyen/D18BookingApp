const mongoose = require('mongoose');

const bookingSchema = new mongoose.Schema({
  id: { type: Number, required: true, unique: true },
  bookingDate: String,
  checkinDate: String,
  checkoutDate: String,
  pricePerDay: Number,
  numberOfDays: Number,
  siteFee: Number,
  roomId: Number,
  userId: Number,
  totalFee: Number,
  currencySymbol: String,
  lastUpdated: Number,
  bookingReview: String,
  refund: Boolean,
  complete: Boolean
});

module.exports = mongoose.model('Booking', bookingSchema);