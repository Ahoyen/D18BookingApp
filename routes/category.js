// routes/categories.js
const express = require('express');
const router = express.Router();
const Category = require('../models/Category');

router.post('/api/categories', async (req, res) => {
    try {
      const { name, icon } = req.body;
  
      if (!name || !icon) {
        return res.status(400).json({ success: false, error: 'Missing name or icon' });
      }
  
      const lastCategory = await Category.findOne().sort({ id: -1 }).lean();
  
      let nextId = 1;
      if (lastCategory && typeof lastCategory.id === 'number') {
        nextId = lastCategory.id + 1;
      }
  
      const category = new Category({ id: nextId, name, icon });
      await category.save();
  
      res.status(201).json({ success: true, data: category });
    } catch (error) {
      console.error(error);
      res.status(500).json({ success: false, error: 'Server error' });
    }
  });
  
// GET all categories
router.get('/api/categories', async (req, res) => {
    try {
      const categories = await Category.find({}, { _id: 0, id: 1, name: 1, icon: 1 }); 
      res.status(200).json({
        success: true,
        data: categories
      });
    } catch (error) {
      res.status(500).json({
        success: false,
        error: 'Server error'
      });
    }
  });
module.exports = router;