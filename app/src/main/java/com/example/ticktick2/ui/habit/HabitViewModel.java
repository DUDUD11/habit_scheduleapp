package com.example.ticktick2.ui.habit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ticktick2.R;
import com.example.ticktick2.dataobject.habit;

public class HabitViewModel extends ViewModel {
    public MutableLiveData<habit> newHabit;
    public MutableLiveData<habit> Selected;

    public boolean MorningCheckbox = false;
    public boolean AfternoonCheckbox = false;
    public boolean NightCheckbox = false;
    public boolean etcCheckbox = false;

    public boolean editinghabit;



    public final String[] titles={
            "데일리 체크인",
            "12시 전에 눕기",
            "자기 전 양치하기",
            "책 읽기",
            "손발톱 자르기",
            "휴식의 날",
            "충전의 날",
            "화를 내지 않다",
            "불평 줄이기",
            "일찍 일어나기",
            "일찍 자기",
            "과일 먹기",
            "운동",
            "스트레칭",
            "쓰기",
            "일기 쓰기",
            "영화 감상",
            "TV 보기",
            "드라마 보기",
            "다른 사람 돕기",
            "청소하기",
            "물 마시기",
            "약 복용하기",
            "피부 관리 하기",
            "몸무게 재기",
            "산책하기",
            "산책/잠깐 나들이/바깥바람쐬기",
            "목/어깨통증",
            "슬프거나 우울함",
            "맑음",
            "명상",
            "오늘 하루 계획하기",
            "약속",
            "일정짜기",
            "해야할 일/투두리스트",
            "긍정적인 생각하기",
            "자신을 가꾸기",
            "더티워즈 없음",
            "날 위해 웃기",
            "샤워하기",
            "내 안의 사랑 지키기/인류애 충전",
            "음악 듣기",
            "지출 기록하기",
            "단백질 섭취",
            "샐러드 먹기",
            "아침 식사/브런치",
            "귀여운 거 보기",
            "배움",
            "새로운 단어 배우기",
            "책 보기/공부",
            "배우기/복습하기",
            "아이디어 찾기/기록",
            "밤공부",
            "패스트푸드 금지",
            "간식 금지"





    };

    public final Integer[] images =
            {
                    R.drawable.ic_daily_c,
                    R.drawable.ic_sleep_c,
                    R.drawable.ic_brushing_c,
                    R.drawable.ic_book_c,
                    R.drawable.ic_nailcut_c,
                    R.drawable.ic_relaxingday_c_h,
                    R.drawable.ic_rechargingday_c_h,
                    R.drawable.ic_dontbeangry_c,
                    R.drawable.ic_dontgripe_c,
                    R.drawable.ic_sun_c,
                    R.drawable.ic_moon_c,
                    R.drawable.ic_fruit_c,
                    R.drawable.ic_exercise_c,
                    R.drawable.ic_stretching_c,
                    R.drawable.ic_write_c,
                    R.drawable.ic_diary_c,
                    R.drawable.ic_movie_c,
                    R.drawable.ic_tv_c,
                    R.drawable.ic_drama_c,
                    R.drawable.ic_helppeople_c,
                    R.drawable.ic_clean_c,
                    R.drawable.ic_drinkwater_c,
                    R.drawable.ic_pill_c,
                    R.drawable.ic_skincare_c,
                    R.drawable.ic_weight_c,
                    R.drawable.ic_walk_c,
                    R.drawable.ic_freshair_c,
                    R.drawable.ic_neckache_c,
                    R.drawable.ic_sadnblue_c,
                    R.drawable.ic_medi_c,
                    R.drawable.ic_meditation_c,
                    R.drawable.ic_plancheck_c,
                    R.drawable.ic_promise_c,
                    R.drawable.ic_plan_c,
                    R.drawable.ic_todolist_c,
                    R.drawable.ic_positive_c,
                    R.drawable.ic_lovable,
                    R.drawable.ic_nodirtywords_c,
                    R.drawable.ic_smile_c,
                    R.drawable.ic_shower_c,
                    R.drawable.ic_humanity_c,
                    R.drawable.ic_music_c,
                    R.drawable.ic_moneycheck_c,
                    R.drawable.ic_protein,
                    R.drawable.ic_salad,
                    R.drawable.ic_brunch,
                    R.drawable.ic_lovable2,
                    R.drawable.ic_study,
                    R.drawable.ic_study2,
                    R.drawable.ic_study3,
                    R.drawable.ic_study4,
                    R.drawable.ic_idea,
                    R.drawable.ic_study5,
                    R.drawable.ic_nofastfood,
                    R.drawable.ic_noicecream





            };



    public final Integer[] doneimage =
            {
                    R.drawable.check9_green2,
                    R.drawable.check16_purple2,
                    R.drawable.check18_black,
                    R.drawable.check5_yellow1,
                    R.drawable.check6_yellow2,
                    R.drawable.check18_black,
                    R.drawable.check18_black,
                    R.drawable.check12_blue2,
                    R.drawable.check10_green3,
                    R.drawable.check6_yellow2,
                    R.drawable.check16_purple2,
                    R.drawable.check5_yellow1,
                    R.drawable.check11_blue1,
                    R.drawable.check8_green1,
                    R.drawable.check6_yellow2,
                    R.drawable.check2_red2,
                    R.drawable.check6_yellow2,
                    R.drawable.check14_pink,
                    R.drawable.check5_yellow1,
                    R.drawable.check2_red2,
                    R.drawable.check4_orange2,
                    R.drawable.check11_blue1,
                    R.drawable.check7_yellow3,
                    R.drawable.check2_red2,
                    R.drawable.check11_blue1,
                    R.drawable.check14_pink,
                    R.drawable.check6_yellow2,
                    R.drawable.check18_black,
                    R.drawable.check18_black,
                    R.drawable.check9_green2,
                    R.drawable.check1_red1,
                    R.drawable.check12_blue2,
                    R.drawable.check13_blue3,
                    R.drawable.check13_blue3,
                    R.drawable.check12_blue2,
                    R.drawable.check8_green1,
                    R.drawable.check8_green1,
                    R.drawable.check14_pink,
                    R.drawable.check14_pink,
                    R.drawable.check12_blue2,
                    R.drawable.check1_red1,
                    R.drawable.check15_purple1,
                    R.drawable.check6_yellow2,
                    R.drawable.check8_green1,
                    R.drawable.check8_green1,
                    R.drawable.check8_green1,
                    R.drawable.check14_pink,
                    R.drawable.check6_yellow2,
                    R.drawable.check18_black,
                    R.drawable.check14_pink,
                    R.drawable.check14_pink,
                    R.drawable.check6_yellow2,
                    R.drawable.check9_green2,
                    R.drawable.check9_green2,
                    R.drawable.check2_red2
            };



    public final String[] texts =
            {
                    "조금 더 나은 하루를 만들어봐요! 하루시작(시간기록/한줄일기)",
                    "푹 자고 좋은 꿈 꿔요",
                    "충치를 예방해요",
                    "하루 한 장 읽으면, 큰 변화가 올 거야",
                    "깔끔하게 하고 다니면 기분이 상큼해요",
                    "오늘은 휴식하는 날! 쉬어요",
                    "오늘은 충전하는 날! 기운을 보충해요",
                    "차분하게 대처하면 좋은 결과가 있을 거예요",
                    "불만을 줄이면 마음의 평화가 찾아옵니다",
                    "생산적인 아침을 만들어요",
                    "푹 자고 좋은 꿈 꿔요",
                    "더 건강하고 더 행복해져요",
                    "운동은 건강한 삶의 기초",
                    "몸의 유연성과 편안함을 유자하세요",
                    "떠오르는 영감을 몇 가지 기록해 두세요",
                    "일기는 나의 과거와 현재, 그리고 미래를 연결하는 다리",
                    "영화는 또다른 현실세계/상상의 세계로 들어가는 문",
                    "좋은 프로그램은 우리의 상상력을 자극하고 마음을 풍요롭게 해요",
                    "드라마는 우리의 일상에 감동과 재미를 더해줘요",
                    "돕고 베푸는 것의 행복을 느껴요",
                    "깨끗한 공간은 맑은 마음을 만들어요",
                    "체내 수분을 유지해요",
                    "약 먹을 시간이에요!",
                    "좋은 피부는 매일의 작은 노력에서 시작됩니다",
                    "몸무게를 재고 건강하게 유지해요",
                    "매일 걷는 한 시간이 당신의 건강을 지킵니다",
                    "콧바람을 쐬고 몸과 마음과 정신을 환기시켜요",
                    "목과 어깨가 아파요 목 운동과 스트레칭을 해요",
                    "지쳤을 땐 쉬고 힐링푸드를 먹고 좋은 걸 봐요",
                    "평온해요",
                    "명상을 꾸준히 하면 내면의 힘을 발견하게 될 거예요",
                    "오늘은 긍정적인 날이 될 거예요!",
                    "약속이 있어요",
                    "미리 일정을 짜요",
                    "해야할 것들을 정리해요",
                    "긍정적인 태도는 행복의 시작입니다",
                    "나를 사랑하는 첫 걸음",
                    "상냥한 말 한마디가 천 냥 빚을 갚습니다",
                    "웃으면 복이 와요",
                    "상쾌한 샤워로 하루를 시작해요",
                    "내 안의 사랑을 지켜나가요",
                    "좋은 음악은 당신을 좋은 곳으로 이끌어줄 거예요",
                    "재정적인 지혜를 얻어라",
                    "단백질 섭취하고 활력을 챙겨요",
                    "신선한 샐러드는 내 몸을 맑게 만들어줘요",
                    "좋은 하루는 아침 식사 후에 시작돼요",
                    "작은 순간들이 모여서 그 사람을 이뤄요 날 행복하게 하는 순간들을 만들어요",
                    "교육의 위대한 목적은 지식이 아니라 행동입니다",
                    "작은 물방울이 모여 큰 바다를 이뤄요",
                    "배움은 또 다른 길을 열어줘요",
                    "배우고 10분 후,1일 후, 1주일 후, 1개월 후 복습해요",
                    "좋은 아이디어가 떠오를 때, 즉시 기록하고 미루지 마세요",
                    "잠이 안 올 땐, 공부하거나 책을 읽어요",
                    "고속노화보단 저속노화를",
                    "충동적인 간식은 이제 그만!"



            };


    public HabitViewModel()
    {
        newHabit = new MutableLiveData<>();
        Selected = new MutableLiveData<>();
    }

    public void resetNewHabit()
    {
        habit tmp = new habit();
        editinghabit=false;
        setNewHabit(tmp);
    }

    public habit getNewHabit() {
        return newHabit.getValue();
    }

    public void setNewHabit(habit p_habit) {

        newHabit.setValue(p_habit);
    }

    public habit getSelectedHabit() {
        return Selected.getValue();
    }

    public void setSelectedHabit(habit p_habit) {

        Selected.setValue(p_habit);
    }



}
