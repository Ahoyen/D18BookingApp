// routes/wishlist.js
const express = require('express');
const router = express.Router();
const Wishlist = require('../models/Wishlist');
const Room = require('../models/Room'); 
const authMiddleware = require('../middleware/auth'); 

router.use(authMiddleware);

// GET /api/user/wishlists - trả về danh sách chi tiết wishlist (room info)
router.get('/api/user/wishlists', async (req, res) => {
  try {
    const wishlist = await Wishlist.findOne({ userId: req.user._id }).populate('roomIds');
    if (!wishlist) {
      return res.json({ success: true, data: [], error: null });
    }
    const data = wishlist.roomIds.map(room => ({
      id: room._id,
      images: room.images || [], 
    }));

    res.json({ success: true, data, error: null });
  } catch (error) {
    res.status(500).json({ success: false, data: null, error: error.message });
  }
});

// GET /api/user/wishlists/ids - chỉ trả về id của phòng đã thích
router.get('/api/user/wishlists/ids', async (req, res) => {
  try {
    const wishlist = await Wishlist.findOne({ userId: req.user._id });
    const data = wishlist ? wishlist.roomIds.map(id => id.toString()) : [];
    res.json({ success: true, data, error: null });
  } catch (error) {
    res.status(500).json({ success: false, data: null, error: error.message });
  }
});

// PUT /api/user/add-to-favoritelists/:id - thêm 1 phòng vào wishlist
router.put('/api/user/add-to-favoritelists/:id', async (req, res) => {
  try {
    const roomId = req.params.id;
    let wishlist = await Wishlist.findOne({ userId: req.user._id });
    if (!wishlist) {
      wishlist = new Wishlist({ userId: req.user._id, roomIds: [] });
    }
    if (!wishlist.roomIds.includes(roomId)) {
      wishlist.roomIds.push(roomId);
      await wishlist.save();
    }
    res.json({ success: true, data: "Added successfully", error: null });
  } catch (error) {
    res.status(500).json({ success: false, data: null, error: error.message });
  }
});

// PUT /api/user/remove-from-favoritelists/:id - xóa 1 phòng khỏi wishlist
router.put('/api/user/remove-from-favoritelists/:id', async (req, res) => {
  try {
    const roomId = req.params.id;
    const wishlist = await Wishlist.findOne({ userId: req.user._id });
    if (!wishlist) {
      return res.status(404).json({ success: false, data: null, error: "Wishlist not found" });
    }
    wishlist.roomIds = wishlist.roomIds.filter(id => id.toString() !== roomId);
    await wishlist.save();
    res.json({ success: true, data: "Removed successfully", error: null });
  } catch (error) {
    res.status(500).json({ success: false, data: null, error: error.message });
  }
});

module.exports = router;
