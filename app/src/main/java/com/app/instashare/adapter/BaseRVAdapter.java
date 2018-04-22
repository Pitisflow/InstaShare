package com.app.instashare.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Pitisflow on 21/4/18.
 */

public abstract class BaseRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<Object> itemList;
    private ArrayList<Integer> itemTypeList;



    public BaseRVAdapter() {
        this.itemList = new ArrayList<>();
        this.itemTypeList = new ArrayList<>();
    }



    public void addCard(Object card, int type)
    {
        itemList.add(card);
        itemTypeList.add(type);

        notifyItemInserted(itemList.size() - 1);
    }

    public boolean addCardAtIndex(Object card, int type, int index)
    {
        if(index >= 0 && index <= itemList.size()) {
            itemList.add(index, card);
            itemTypeList.add(index, type);

            notifyItemInserted(index);
            return true;
        }

        return false;
    }

    public void addCards(ArrayList<Object> cards, int type)
    {
        int currentIndex = itemList.size() == 0 ? 0 : itemList.size() - 1;

        itemList.addAll(cards);

        for (int i = 0; i < cards.size(); i++)
        {
            itemTypeList.add(type);
        }

        notifyItemRangeInserted(currentIndex, cards.size());
    }

    public boolean modifyCardAtIndex(Object card, int type, int index)
    {
        if(index >= 0 && index <= itemList.size()) {
            itemList.set(index, card);
            itemTypeList.set(index, type);

            notifyItemChanged(index);
            return true;
        }

        return false;
    }

    public void removeFirstCard()
    {
        itemList.remove(0);
        itemTypeList.remove(0);

        notifyItemRemoved(0);
    }

    public void removeLastCard()
    {
        int lastIndex = itemList.size() == 0 ? 0 : itemList.size() - 1;

        itemList.remove(lastIndex);
        itemTypeList.remove(lastIndex);

        notifyItemRemoved(lastIndex);
    }

    public void removeAllCards()
    {
        if (itemList.size() > 0)
        {
            itemList.clear();
            itemTypeList.clear();

            notifyDataSetChanged();
        }
    }




    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return itemTypeList.get(position);
    }





    public ArrayList<Object> getItemList() {
        return itemList;
    }

    public ArrayList<Integer> getItemTypeList() {
        return itemTypeList;
    }
}
