// models/Wishlist.js
const mongoose = require('mongoose');

const WishlistSchema = new mongoose.Schema({
  userId: { type: Number, required: true, unique: true },
  roomIds: [{ type: Number, required: true, unique: true }] 
});

module.exports = mongoose.model('Wishlist', WishlistSchema);
