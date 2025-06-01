const express = require('express');
const router = express.Router();
const bcrypt = require('bcryptjs');
const User = require('../models/User');
const Role = require('../models/Role');

// POST /api/auth/register
router.post('/api/auth/register', async (req, res) => {
  try {
    const { firstName, lastName, phoneNumber, email, password, sex, birthday, avatarPath } = req.body;

    // Validate required fields
    if (!firstName || !lastName || !email || !password) {
      return res.status(400).json({
        success: false,
        data: null,
        error: 'Missing required fields'
      });
    }

    // Check if email already exists
    const existingUser = await User.findOne({ email });
    if (existingUser) {
      return res.status(400).json({
        success: false,
        data: null,
        error: 'Email already registered'
      });
    }

    // Hash password
    const hashedPassword = await bcrypt.hash(password, 10);

    // Auto-increment id
    const lastUser = await User.findOne().sort({ id: -1 }).lean();
    const nextId = lastUser ? lastUser.id + 1 : 1;

    const userRole = await Role.findOne({ id: 1 }); 

    // Normalize sex value
    const sexNormalized = sex
      ? sex.charAt(0).toUpperCase() + sex.slice(1).toLowerCase()
      : undefined;

    const currentTime = Date.now();
  
    // Create new user document
    const newUser = new User({
      id: nextId,
      status: true,
      createdDate: currentTime,
      updatedDate: currentTime,
      firstName,
      lastName,
      fullName: `${firstName} ${lastName}`,
      email,
      password: hashedPassword,
      phoneNumber,
      sex: sexNormalized,
      birthday,
      phoneVerified: false,
      supremeHost: false,
      avatarPath: avatarPath || null 
    });

    await newUser.save();

    // Remove password before sending response
    const { password: _, ...userData } = newUser._doc;

    res.status(201).json({
      success: true,
      data: userData,
      error: null
    });
  } catch (error) {
    console.error('Register error:', error);
    res.status(500).json({
      success: false,
      data: null,
      error: 'Server error'
    });
  }
});

// POST /api/auth/login
router.post('/api/auth/login', async (req, res) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      return res.status(400).json({
        success: false,
        data: null,
        error: 'Missing email or password'
      });
    }
    const user = await User.findOne({ email }).populate('role').select('+password');
    if (!user) {
      return res.status(400).json({
        success: false,
        data: null,
        error: 'Invalid credentials'
      });
    }
    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) {
      return res.status(400).json({
        success: false,
        data: null,
        error: 'Invalid credentials'
      });
    }
    // Set HttpOnly cookie for session 
    res.setHeader('Set-Cookie', `sessionId=${user.id}; HttpOnly; Path=/;`);

    const { password: _, ...userData } = user._doc;
    userData.fullName = `${user.firstName} ${user.lastName}`;
    userData.createdDate = new Date(user.createdAt).getTime();
    userData.updatedDate = new Date(user.updatedAt).getTime();
    
    if (typeof userData.about !== 'object') userData.about = null;
    if (typeof userData.addressDetails !== 'object') userData.addressDetails = null;

    res.status(200).json({
      success: true,
      data: userData,
      error: null
    });
  } catch (error) {
    console.error('Login error:', error);
    res.status(500).json({
      success: false,
      data: null,
      error: 'Server error'
    });
  }
});
// GET /api/user/profile
router.get('/api/user/profile', async (req, res) => {
    try {
      // const sessionId = req.headers.cookie 
      const sessionId = req.cookies.sessionId; //cookie-parser
      if (!sessionId) {
        return res.status(401).json({ success: false, error: 'Unauthorized' });
      }
      // const user = await User.findById(sessionId, { password: 0, _id: 0, __v: 0 });
        // Tìm user theo id trong sessionId
      const userId = parseInt(req.cookies.sessionId, 10);
      const user = await User.findOne({ id: userId }).select('-password -_id -__v');
  
      if (!user) {
        return res.status(404).json({ success: false, error: 'User not found' });
      }
  
      res.status(200).json({ success: true, data: user });
    } catch (error) {
      console.error(error);
      res.status(500).json({ success: false, error: 'Server error' });
    }
  });
// PUT /api/auth/reset-password
router.put('/api/auth/reset-password', async (req, res) => {
    const { userEmail, newPassword, confirmNewPassword } = req.body;

    if (newPassword !== confirmNewPassword) {
        return res.status(400).json({ error: 'Passwords do not match' });
    }

    const user = await User.findOne({ email: userEmail });
    if (!user) {
        return res.status(404).json({ error: 'User not found' });
    }

    const hashedPassword = await bcrypt.hash(newPassword, 10);
    user.password = hashedPassword;
    await user.save();

    res.json({ message: 'Password updated successfully' });
  });
  
module.exports = router;