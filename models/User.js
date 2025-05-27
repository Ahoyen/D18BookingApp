// const mongoose = require('mongoose');

// const userSchema = new mongoose.Schema({
//   firstName: { type: String, required: true },  
//   lastName: { type: String, required: true },   
//   phoneNumber: { type: String },  
//   sex: { type: String, enum: ['Male', 'Female', 'Other'] },                 
//   birthday: { type: Date },
//   email: { type: String, required: true, unique: true },
//   password: { type: String, required: true }
// }, { timestamps: true });

// const User = mongoose.model('User', userSchema);

// module.exports = User;

const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
  id: { type: Number, unique: true },  // id int tự tăng
  firstName: { type: String, required: true },
  lastName: { type: String, required: true },
  phoneNumber: { type: String },
  sex: { type: String, enum: ['Male', 'Female', 'Other'] },
  birthday: { type: Date },
  email: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  role: { type: String, default: 'user' }, // thêm role nếu cần
  avatarPath: { type: String },
  fullName: { type: String },
  cookie: { type: String },
  phoneVerified: { type: Boolean, default: false },
  about: { type: String },
  fullPathAddress: { type: String },
  supremeHost: { type: Boolean, default: false }
}, { timestamps: true });

const User = mongoose.model('User', userSchema);

module.exports = User;