const mongoose = require('mongoose');

const ReviewRatingSchema = new mongoose.Schema({
  cleanliness: { type: Number, required: true, min: 1, max: 5 },
  contact: { type: Number, required: true, min: 1, max: 5 },
  checkin: { type: Number, required: true, min: 1, max: 5 },
  accuracy: { type: Number, required: true, min: 1, max: 5 },
  location: { type: Number, required: true, min: 1, max: 5 },
  value: { type: Number, required: true, min: 1, max: 5 }
});

const ReviewSchema = new mongoose.Schema({
  customerAvatar: { type: String, required: true },
  reviewRating: { type: ReviewRatingSchema, required: true },
  comment: { type: String, required: true },
  customerName: { type: String, required: true },
  createdAt: { type: Date, default: Date.now },
  roomId: { type: Number, required: true }, 
  userId: { type: Number, required: true } 
});

module.exports = mongoose.model('Review', ReviewSchema);