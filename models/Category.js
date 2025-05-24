// models/Category.js
const mongoose = require('mongoose');

const categorySchema = new mongoose.Schema({
    id: {
      type: Number,
      required: true,
      unique: true,
    },
    name: {
      type: String,
      required: true
    },
    icon: {
      type: String
    }
  });
  

module.exports = mongoose.model('Category', categorySchema);
