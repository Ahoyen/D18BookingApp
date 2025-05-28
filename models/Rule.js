const mongoose = require('mongoose');

const RuleSchema = new mongoose.Schema({
  id: { type: Number, required: true, unique: true },
  status: Boolean,
  createdDate: mongoose.Schema.Types.Mixed,  
  updatedDate: mongoose.Schema.Types.Mixed,
  title: String,
  icon: String,
  iconPath: String
});

module.exports = mongoose.model('Rule', RuleSchema);
