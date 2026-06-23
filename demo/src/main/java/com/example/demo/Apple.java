package com.example.demo;

import java.util.*;

public class Apple {

    public static void main(String[] args) {
        System.out.println("apple");
       // longtestSubstring();
      // slidingWindowMax();
     //   sumofTwo1();
       // logestSubscting();
        firstNonRepeated();
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

    public static void sumofTwo(){
        int[] nums = new int[]{1,3,5,6,7,9,4};
        int target = 10;
        Set<Integer> map = new HashSet<>();
        List<int[]> result = new ArrayList<>();
        for(int num : nums){
            int existing = target-num;
          //  System.out.println(map+"-->"+map.contains(existing)) ;
            if(map.contains(existing)){
               // System.out.println(num +"+"+existing);
                result.add(new int[]{existing,num});

            }
            map.add(num);
        }
        result.stream().forEach(pair -> System.out.println(Arrays.toString(pair)));

    }

    public static void sumofTwo1(){
        List<Integer> list = List.of(1,3,5,6,7,9,4);
        int target = 10;
        Map<Integer,Integer>  map = new HashMap<>();
        List<int[]> result = new ArrayList<>();
        for(int i=0; i<list.size();i++)
        {
            int compliment =  target - list.get(i);
         if(map.containsKey(compliment))  {
             result.add(new int[]{compliment,list.get(i)});
         }
         map.put(list.get(i),1);

        }
        System.out.println(result);
        result.stream().forEach(pair -> Arrays.toString(pair));

    }

    public static void logestSubscting(){
        String str = "abcabcbb";
        int left = 0;
        List<String> result = new ArrayList<>();
        Set<Character> set = new LinkedHashSet<>();
        for(int right=0;right<str.length();right++){
          if(set.contains(str.charAt(right))) {
              result.add(Arrays.toString(set.toArray()));
              set.remove(str.charAt(left));
              left++;

          }
          set.add(str.charAt(right));



        }
        result.add(Arrays.toString(set.toArray()));

      System.out.println(result);
    }

    public static void firstNonRepeated(){

        String str =  "aabbccddef";
       Set<Character> unitque = new LinkedHashSet<>();
       Set<Character> duplicates = new LinkedHashSet<>();


        for(int i=0;i<str.length();i++){
           if(unitque.contains(str.charAt(i))){
               duplicates.add(str.charAt(i));
               unitque.remove(str.charAt(i));
           }
           else if(!duplicates.contains(str.charAt(i)))

               unitque.add(str.charAt(i));
        }
        System.out.println(unitque.stream().findFirst());



    }




   
     


   }
    


