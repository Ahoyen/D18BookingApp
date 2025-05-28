const mongoose = require('mongoose');

const AmenitySchema = new mongoose.Schema({
  id: { type: Number, required: true, unique: true },
  icon: String,
  name: String
});

module.exports = mongoose.model('Amenity', AmenitySchema);