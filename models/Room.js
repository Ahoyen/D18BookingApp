// const mongoose = require('mongoose');

// const RoomSchema = new mongoose.Schema({
//   id: { type: Number, required: true, unique: true }, // ID tự tăng
//   name: { type: String, required: true },
//   categoryId: { type: Number, required: true },       // categoryId dạng số nguyên

//   thumbnail: String,
//   images: [String],
//   likedByUsers: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
//   price: Number,
//   currencySymbol: String,
//   stayType: String,
// });

// module.exports = mongoose.model('Room', RoomSchema);
const RoomSchema = new mongoose.Schema({
  id: { type: Number, required: true, unique: true },
  name: String,
  categoryId: { type: Number, required: true }, 
  thumbnail: String,
  images: [String],
  likedByUsers: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
  price: Number,
  currencySymbol: String,
  currencyUnit: String,

  stayType: String,
  description: String,
  latitude: Number,
  longitude: Number,
  location: String,
  cityName: String,

  guest: Number,
  bedroom: Number,
  bathroom: Number,
  bed: Number,

  amenities: [{ type: Number, ref: 'Amenity' }],
  rules: [{ type: Number, ref: 'Rule' }],
  host: { type: Number, ref: 'User' },
});
module.exports = mongoose.model('Room', RoomSchema);