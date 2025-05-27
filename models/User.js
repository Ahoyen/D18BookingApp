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
  id: { type: Number, unique: true },  
  status: { type: Boolean, default: true },
  createdDate: { type: Number }, // timestamp
  updatedDate: { type: Number }, // timestamp
  firstName: { type: String, required: true },
  lastName: { type: String, required: true },
  sex: { type: String, enum: ['Male', 'Female', 'Other'] },

  birthday: { type: String },
  email: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  phoneNumber: { type: String },
  phoneVerified: { type: Boolean, default: false },
  cookie: { type: String },
  role: { 
    type: String,
    enum: ['user', 'admin', 'host'],
    default: 'user'
  },
  about: { type: mongoose.Schema.Types.Mixed },
  fullName: { type: String },
  avatarPath: { type: String },
  fullPathAddress: { type: String },
  addressDetails: {
    type: mongoose.Schema.Types.Mixed // hoặc bạn định nghĩa 1 schema riêng cho AddressDetails
  },
  supremeHost: { type: Boolean, default: false }
}, { timestamps: true }); 

const User = mongoose.model('User', userSchema);

module.exports = User;
