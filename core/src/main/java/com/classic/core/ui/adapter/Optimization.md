## [RecyclerView 优化](https://blog.mindorks.com/recyclerview-optimization)


#### 设置图像宽度和高度

如果我们的图像宽度和高度是动态的（不是固定的），并且我们是imageUrl从服务器获取的。
如果没有设置正确的图像宽度和高度，在加载（图像下载）和图像设置到ImageView的过渡期间，UI会闪烁。
因此，应该让我们的后端开发人员发送图像大小或纵横比，相应地，我们可以计算出所需的 ImageView 的宽度和高度。
然后，我们将只能预先设置宽度和高度。因此没有闪烁。问题解决了！


#### 在 onBindViewHolder 方法中少做
#### 避免嵌套视图


#### 使用 RecyclerView API

 *  使用 `DiffUtil`
 *  使用 `setHasFixedSize`
    如果所有项目的高度相等，我们应该使用这种方法。
    ```
    recyclerView.setHasFixedSize(true)
    ```
 *  通知API
    ```
    adapter.notifyItemRemoved(position)
    adapter.notifyItemChanged(position)
    adapter.notifyItemInserted(position)
    adapter.notifyItemRangeInserted(start, end)
    ```
 *  被迫 `notifyDataSetChanged()` 时，尝试使用 `setHasStableIds(true)`
    它指示数据集中的每个项目是否可以用 Long 类型的唯一标识符表示。
    即使我们调用notifyDataSetChanged()，它也不必处理整个适配器的完整重新排序，因为它可以找到某个位置的项目是否与之前相同并且做的工作更少。


#### 使用 setRecycledViewPool 优化嵌套 RecyclerView

这将提高滚动性能，因为它重用共享 ViewPool 中的视图。

```
- OuterRecyclerView
    - InnerRecyclerViewOne
    - InnerRecyclerViewTwo

class OuterRecyclerViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        viewHolderOne.innerRecyclerViewOne.setRecycledViewPool(viewPool)
        viewHolderTwo.innerRecyclerViewTwo.setRecycledViewPool(viewPool)
    }
}
```

#### 使用 setItemViewCacheSize

根据官方文档：在将它们添加到潜在共享的回收视图池之前，它设置要保留的屏幕外视图的数量。
屏幕外视图缓存始终了解附加适配器中的更改，从而允许 LayoutManager 重用那些未修改的视图，而无需返回适配器重新绑定它们。
这意味着当我们滚动 RecyclerView 使得视图几乎完全离开屏幕时， RecyclerView 会将它保留在周围，
以便我们可以将其滚动回视图而无需onBindViewHolder()再次调用。
一般情况下，我们不会改变View Cache的大小，而是尝试一下，如果对你有用，就实现它。