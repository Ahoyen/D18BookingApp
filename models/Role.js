const mongoose = require('mongoose');

const roleSchema = new mongoose.Schema({
  id: { type: Number, unique: true, required: true },
  name: { type: String, required: true }
});

const Role = mongoose.model('Role', roleSchema);

module.exports = Role;