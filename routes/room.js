const express = require('express');
const mongoose = require('mongoose');
const router = express.Router();
const Room = require('../models/Room');

router.post('/api/rooms', async (req, res) => {
    try {
      const { name, categoryId, thumbnail, images, likedByUsers, price, currencySymbol, stayType } = req.body;
  
      if (!name || typeof categoryId !== 'number') {
        return res.status(400).json({ success: false, message: 'Room name and categoryId (number) are required' });
      }
  
      // Lấy id tăng dần cho Room
      const lastRoom = await Room.findOne().sort({ id: -1 }).lean();
      const nextId = lastRoom && typeof lastRoom.id === 'number' ? lastRoom.id + 1 : 1;
  
      const newRoom = new Room({
        id: nextId,
        name,
        categoryId, 
        thumbnail,
        images,
        likedByUsers,
        price,
        currencySymbol,
        stayType,
      });
  
      await newRoom.save();
      res.status(201).json({ success: true, data: newRoom });
    } catch (err) {
      console.error(err);
      res.status(500).json({ success: false, message: 'Server error' });
    }
  });
  
  

// GET all room
router.get('/api/rooms', async (req, res) => {
    try {
      const { categoryId, query } = req.query;
      const filter = {};
  
      if (categoryId && !isNaN(categoryId)) {
        filter.categoryId = parseInt(categoryId); 
      }
  
      if (query) {
        filter.name = { $regex: query, $options: 'i' };
      }
  
      const rooms = await Room.find(filter);
      res.json({ success: true, data: rooms });
    } catch (err) {
      console.error(err);
      res.status(500).json({ success: false, message: 'Server error' });
    }
  });
  
module.exports = router;