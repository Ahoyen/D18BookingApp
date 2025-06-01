const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
  id: { type: Number, unique: true, required: true },    
  status: { type: Boolean, default: true },
  createdDate: { type: Number, default: () => Date.now() }, 
  updatedDate: { type: Number, default: () => Date.now() }, 
  firstName: { type: String, required: true },
  lastName: { type: String, required: true },
  sex: { type: String, enum: ['Male', 'Female', 'Other'] },
  birthday: { type: String },  
  email: { type: String, required: true, unique: true },
  cookie: { type: String },
  roleId: { type: Number, required: true, default: 1 }, // Default roleId = 1 (user)
  role: { type: mongoose.Schema.Types.ObjectId, ref: 'Role' },
  phoneNumber: { type: String },
  phoneVerified: { type: Boolean, default: false },
  about: { type: mongoose.Schema.Types.Mixed, default: null },
  fullName: { type: String },
  avatarPath: { type: String },
  fullPathAddress: { type: String },
  password: { type: String, required: true },
  addressDetails: { type: mongoose.Schema.Types.Mixed, default: null },
  supremeHost: { type: Boolean, default: false }
}, { timestamps: true });

userSchema.pre('save', function(next) {
  this.fullName = `${this.firstName} ${this.lastName}`;
  this.updatedDate = Date.now();
  if (!this.createdDate) {
    this.createdDate = this.updatedDate;
  }
  next();
});

const User = mongoose.model('User', userSchema);

module.exports = User;
