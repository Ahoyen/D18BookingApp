const mongoose = require('mongoose');

const ReviewSchema = new mongoose.Schema({
  customerAvatar: { type: String, required: true }, 
  rating: {
    type: Number,
    required: true,
    min: 1,
    max: 5
  },
  comment: { type: String, required: true },
  customerName: { type: String, required: true },
  createdAt: { type: Date, default: Date.now },
  roomId: { type: Number, required: true } 
});

module.exports = mongoose.model('Review', ReviewSchema);