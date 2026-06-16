package com.example.demo;

import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
public class Apple {

    public static void main(String[] args) {
        System.out.println("apple");
       // longtestSubstring();
       slidingWindowMax();
    }
    public static void longtestSubstring(){
        String str= "abcabcbb";
        Set<Character> set= new LinkedHashSet<>();
        int left= 0;
        int max = 0;
        for(int right=0;right<str.length();right++){
            if(set.contains(str.charAt(right))){
                set.remove(str.charAt(left));
                left++;
            }
            set.add(str.charAt(right));
            max = Math.max(max,right-left+1);
            System.out.println(set);

        }
        System.out.println(set);
        System.out.println(max);
    }
   public static void slidingWindowMax(){
     int[] nums = {1,3,-1,-3,5,3,6,7};
     int window = 3;
     for(int left = 0; left<nums.length-3+1 ;left++){
        int max = nums[left];
        for(int right= left+1; right<left+window;right++){
            max = max+nums[right];     
         } 
         System.out.println(max);

     }
    }




   
     


   }
    

}
