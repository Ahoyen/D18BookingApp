const express = require('express');
const router = express.Router();
const User = require('../models/User');
const bcrypt = require('bcryptjs');

router.post('/api/auth/register', async (req, res) => {
    try {
        const { firstName, lastName, email, password } = req.body;

        if (!firstName || !lastName || !email || !password) {
            return res.status(400).json({ error: 'Missing required fields' });
        }

        const existingUser = await User.findOne({ email });
        if (existingUser) {
            return res.status(400).json({ error: 'Email already registered' });
        }

        const lastUser = await User.findOne().sort({ id: -1 }).lean();
        let nextId = 1;
        if (lastUser && typeof lastUser.id === 'number') {
          nextId = lastUser.id + 1;
        }

        const hashedPassword = await bcrypt.hash(password, 10);

        const newUser = new User({
            id: nextId,
            firstName,
            lastName,
            email,
            password: hashedPassword
        });

        await newUser.save();

        res.status(201).json({ message: 'User registered successfully', user: newUser });
    } catch (error) {
        console.error(error);
        res.status(500).json({ error: 'Server error' });
    }
});

// POST /api/auth/login
router.post('/api/auth/login', async (req, res) => {
    try {
        const { email, password } = req.body;

        if (!email || !password) {
            return res.status(400).json({ error: 'Missing email or password' });
        }

        const user = await User.findOne({ email });
        if (!user) {
            return res.status(400).json({ error: 'Invalid credentials' });
        }

        const isMatch = await bcrypt.compare(password, user.password);
        if (!isMatch) {
            return res.status(400).json({ error: 'Invalid credentials' });
        }

        res.setHeader('Set-Cookie', `sessionId=${user._id}; HttpOnly; Path=/;`);
        
        // Bỏ mật khẩu khỏi user data trước khi trả về
        const { password: _, ...userData } = user._doc;

        // Trả về response với cấu trúc { success, data }
        res.status(200).json({
            success: true,
            data: userData
        });
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Server error' });
    }
});



// router.get('/api/user/profile', async (req, res) => {
//     try {
//       const sessionId = req.headers.cookie // hoặc req.cookies.sessionId (nếu dùng cookie-parser)
//       if (!sessionId) {
//         return res.status(401).json({ success: false, error: 'Unauthorized' });
//       }
  
//       // Tìm user theo _id trong sessionId
//       const user = await User.findById(sessionId, { password: 0, _id: 0, __v: 0 });
  
//       if (!user) {
//         return res.status(404).json({ success: false, error: 'User not found' });
//       }
  
//       res.status(200).json({ success: true, data: user });
//     } catch (error) {
//       console.error(error);
//       res.status(500).json({ success: false, error: 'Server error' });
//     }
//   });
  
module.exports = router;